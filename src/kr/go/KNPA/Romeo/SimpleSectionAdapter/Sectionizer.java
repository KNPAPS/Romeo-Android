package kr.go.KNPA.Romeo.SimpleSectionAdapter;

/**
 * 인터페이스는 인스턴스들이 서로 비교하는 식으로 그들에게 타이틀을 제공한다.
 */
public interface Sectionizer<T> {

    /**
     * Returns the title for the given instance from the data source.
     * 
     * @param instance The instance obtained from the data source of the decorated list adapter.
     * @return section title for the given instance.
     */
    String getSectionTitleForItem(T instance);
}
