package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public interface GameContentType<TypeData, Elements, Element> {
    TypeData newTypeData(TypedGameContent<TypeData, Elements, Element> content);

    Elements modContent(String modId);

    Iterable<Element> iterate(Elements elements);

    default Collection<? extends Element> elementCollection(Elements elements) {
        var list = new ArrayList<Element>();
        iterate(elements).forEach(list::add);
        return list;
    }

    default String toStringElements(Elements elements) {
        return elementCollection(elements)
                .stream()
                .map(this::toStringElement)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    default String toStringElement(Element element) {
        return Objects.requireNonNull(element.toString());
    }

    ElementsBuilder<Elements, Element> newElementsBuilder();

    interface ElementsBuilder<Elements, Element> {
        void addElements(Elements elements);

        void addElement(Element element);

        Elements build();
    }

    abstract class AbstractElementsBuilder<Elements, Element> implements ElementsBuilder<Elements, Element> {
        private final List<Elements> elementsList = new ArrayList<>();
        private final Set<Element> elementSet = new HashSet<>();

        @Override
        public void addElements(Elements elements) {
            elementsList.add(elements);
        }

        @Override
        public void addElement(Element element) {
            elementSet.add(element);
        }

        @Override
        public Elements build() {
            if (elementSet.isEmpty()) {
                if (elementsList.size() == 1) {
                    return Objects.requireNonNull(elementsList.iterator()).next();
                }
            }
            if (elementsList.isEmpty()) {
                return buildFromElementSet(elementSet);
            }
            if (elementSet.isEmpty()) {
                return buildFromElementsList(elementsList);
            }
            return buildFromBoth(elementsList, elementSet);
        }

        public abstract Elements buildFromElementsList(List<Elements> elementsList);

        public abstract Elements buildFromElementSet(Set<Element> elementSet);

        public abstract Elements buildFromBoth(List<Elements> elementsList, Set<Element> elementSet);
    }
}
