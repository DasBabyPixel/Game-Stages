# Definitions

- ItemStackRestrictionEntrySettings: serializable, information about how an ItemStack should be restricted. Not
  associated with any specific ItemStack.
- ItemStackRestrictionEntry: serializable, Combination of PreparedRestrictionPredicate and
  ItemStackRestrictionEntrySettings
- ItemStackRestrictionResolver: maps ItemStack -> ItemStackRestrictionEntry
- DataInput: serializable, somewhat arbitrary data
- ItemStackRestrictionResolverFactory: maps DataInput -> ItemStackRestrictionResolver

# Builtin factory to restrict entire item

factoryId = "builtin_item"  
data = `<ItemStackRestrictionEntry>`

# Builtin factory to restrict by resolver

Known shared information:  
Map<String, ItemStackRestrictionResolver> knownResolvers  
factoryId = "builtin_resolver"  
data = `<string>`

# Network Transfer

### Known shared information:

- Map<String, ItemStackRestrictionResolverFactory> knownFactories

### Step 1: Send existing ItemStackRestrictionEntries

Collect all ItemStackRestrictionEntries  
Create Map<ItemStackRestrictionEntry, Integer> idMap, mapping each entry to a unique ID  
Send all ItemStackRestrictionEntries and IDs to the client

### Step 2: Send restrictions

For each ResolverRestriction

- if (restriction is DirectRestriction(item, resolver))
    - Send restrictDirect(item, resolver.id())
- else if (restriction is FactoryRestriction(item, factoryId, data))
    - Send restrictFactory(item, factoryId, data)
- else error()

# Compile ItemStack Resolver
