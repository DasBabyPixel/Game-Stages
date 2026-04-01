# Item Restriction Factory — Specification

## Overview

The **Restriction Factory** is a rule-based system that maps an item stack to a **restriction entry** (e.g. a
progression age or access tier). It accepts an item type and a **resolver tree** — a composable, ordered set of
predicate branches — and returns the first restriction entry whose conditions are satisfied.

This is the primary mechanism for gating item availability by progression state, item properties, or any other
inspectable component.

---

## Top-Level Call

```
restrictItemStacksByFactory(itemId: ResourceLocation, resolver: ResolverNode): RestrictionEntry
```

| Parameter  | Type               | Description                                                     |
|------------|--------------------|-----------------------------------------------------------------|
| `itemId`   | `ResourceLocation` | The item type this factory applies to (e.g. `"enchanted_book"`) |
| `resolver` | `ResolverNode`     | The root node of the resolver tree                              |

**Returns:** A `RestrictionEntry` — a named reference to an age, tier, or access group.

---

## ResolverNode Types

A `ResolverNode` is a tagged union dispatched on its `type` field. All resolver nodes must declare a `type`.

### `sequential`

Evaluates its `values` list in order. Returns the `return` value of the **first branch whose condition passes**. If no
branch matches, falls through to `else`.

```jsonc
{
  "type": "sequential",
  "values": [ <BranchNode>, ... ],
  "else": <RestrictionEntry>   // Required fallback
}
```

| Field    | Type               | Required | Description                          |
|----------|--------------------|----------|--------------------------------------|
| `values` | `BranchNode[]`     | ✅        | Ordered list of branches to evaluate |
| `else`   | `RestrictionEntry` | ✅        | Returned when no branch matches      |

> **Design note:** `sequential` short-circuits — evaluation stops at the first passing branch. Order is significant.

---

## BranchNode Types

A `BranchNode` is a single conditional arm within a resolver. It pairs a **condition** with a **return value**.

### `predicate`

Evaluates a data component predicate against the incoming item stack. Returns `return` if the predicate passes.

```jsonc
{
  "type": "predicate",
  "condition": <ComponentPredicate>,
  "return": <RestrictionEntry>
}
```

| Field       | Type                 | Required | Description                                             |
|-------------|----------------------|----------|---------------------------------------------------------|
| `condition` | `ComponentPredicate` | ✅        | A component predicate evaluated against the item        |
| `return`    | `RestrictionEntry`   | ✅        | The restriction entry to return if the predicate passes |

### `always`

Unconditionally returns a restriction entry. Useful as the last branch in a `sequential` list as an explicit catch-all (
alternative to `else`).

```jsonc
{
  "type": "always",
  "return": <RestrictionEntry>
}
```

---

## ComponentPredicate

Conditions use the **Component Predicate Protocol**. The `type` field identifies which component is being inspected.

### `stored_enchantments`

Checks the enchantments stored in an `enchanted_book` (i.e. the `minecraft:stored_enchantments` component).

```jsonc
{
  "type": "stored_enchantments",
  "values": [ <EnchantmentId>, ... ],
  "match": "any" | "all"
}
```

| Field    | Type              | Required | Default | Description                                                   |
|----------|-------------------|----------|---------|---------------------------------------------------------------|
| `values` | `EnchantmentId[]` | ✅        | —       | List of enchantment IDs to match against                      |
| `match`  | `"any" \| "all"`  | ❌        | `"any"` | Whether one or all of the listed enchantments must be present |

### Extensibility

Any Component Predicate type from the predicate protocol may be used here. The `type` field is a namespaced registry
key, so custom types are registered without modifying the factory core.

```jsonc
{ "type": "minecraft:damage",        "min": 1, "max": 100 }
{ "type": "minecraft:enchantments",  "contains": [ { "id": "minecraft:silk_touch" } ] }
{ "type": "custom:my_tag_check",     "tag": "mymod:cursed" }
```

---

## RestrictionEntry

A `RestrictionEntry` is a **named reference** to a restriction definition defined elsewhere in the data pack. It is
always expressed as a `ResourceLocation` string.

```
"stone_age"
"i_s"
"mypack:iron_age"
```

Restriction entries are resolved at evaluation time. An unresolvable entry is a load-time error.

---

## Full Example

*"An enchanted book containing Sharpness is restricted to `i_s`. All other enchanted books fall back to `stone_age`."*

```jsonc
restrictItemStacksByFactory("enchanted_book", {
  "type": "sequential",
  "values": [
    {
      "type": "predicate",
      "condition": {
        "type": "stored_enchantments",
        "values": ["sharpness"]
      },
      "return": "i_s"
    }
  ],
  "else": "stone_age"
})
```

### Extended Example

*"Books with Silk Touch are unrestricted. Books with any curse go to `locked`. Everything else falls to `stone_age`."*

```jsonc
restrictItemStacksByFactory("enchanted_book", {
  "type": "sequential",
  "values": [
    {
      "type": "predicate",
      "condition": {
        "type": "stored_enchantments",
        "values": ["silk_touch"]
      },
      "return": "unrestricted"
    },
    {
      "type": "predicate",
      "condition": {
        "type": "stored_enchantments",
        "values": [
          "binding_curse",
          "vanishing_curse"
        ],
        "match": "any"
      },
      "return": "locked"
    }
  ],
  "else": "stone_age"
})
```

---

## Evaluation Algorithm

```
evaluate(itemStack, resolver):
  switch resolver.type:
    case "sequential":
      for each branch in resolver.values:
        if evaluate_branch(itemStack, branch) is PASS:
          return branch.return
      return resolver.else

evaluate_branch(itemStack, branch):
  switch branch.type:
    case "predicate":
      return evaluate_condition(itemStack, branch.condition)
    case "always":
      return PASS

evaluate_condition(itemStack, condition):
  handler = registry.lookup(condition.type)
  if handler is null:
    return FAIL           // unknown type is a safe failure, not an error
  return handler.test(itemStack, condition)
```

A missing or unrecognised `type` **fails silently** rather than erroring, keeping the factory safe in partial or
misconfigured data packs.

---

## Extensibility Points

| Extension Point       | How to Add                                                                      |
|-----------------------|---------------------------------------------------------------------------------|
| New resolver strategy | Register a new `ResolverNode` type (e.g. `weighted`, `random`, `context_aware`) |
| New branch type       | Register a new `BranchNode` type (e.g. `nbt_check`, `tag_match`)                |
| New condition type    | Register a `ComponentPredicateHandler` under a namespaced ID                    |
| New restriction entry | Define the entry in the restriction registry; no factory changes needed         |

The factory core is intentionally closed to modification — all new behaviour is added through the registry.
