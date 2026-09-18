import {BASE_WINE_FILTERS} from "../constants/options.js";

const FilterSidebar = ({filters, setFilters}) => {

    const toggleFilter = (base) => {
        setFilters((prevFilters) => {
            // All 是互斥選項，選了就只留 All
            if (base === "All" || prevFilters.includes("All")) {
                return base === "All" ? ["All"] : [base];
            }
            // 已選過的再點一次就取消，取消到空的就回到 All
            if (prevFilters.includes(base)) {
                const remaining = prevFilters.filter((f) => f !== base);
                return remaining.length === 0 ? ["All"] : remaining;
            }
            return [...prevFilters, base];
        });
    };

    return (
        <section id="sidebarSection">
            <h3>篩選基酒</h3>
            {BASE_WINE_FILTERS.map((base) => (
                <div id="sidebarDiv" key={base}>
                    <div id="barDiv">
                        <input
                            type="checkbox"
                            checked={filters.includes(base)}
                            onChange={() => toggleFilter(base)}
                        />
                        <label>{base}</label>
                    </div>
                </div>
            ))}
        </section>
    );
};

export default FilterSidebar;
