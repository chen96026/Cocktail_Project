import React from "react";

const FilterSidebar = ({filters, setFilters}) => {
    const bases = ["All", "Vodka", "Brandy", "Gin", "Whisky", "Tequila", "Rum","Others"];

    const toggleFilter = (base) => {
        setFilters((prevFilters) => {
            let newFilters;
            if (base === "All") {
                newFilters = ["All"];
            } else {
                // 移除 "All" 並選擇基酒
                if (prevFilters.includes("All")) {
                    newFilters = [base];
                } else if (prevFilters.includes(base)) {
                    // 若基酒已存在，則取消選擇
                    newFilters = prevFilters.filter((f) => f !== base);
                    // 如果取消後為空，重新設置為 "All"
                    if (newFilters.length === 0) {
                        newFilters = ["All"];
                    }
                } else {
                    // 增加新的基酒並移除 "All"
                    newFilters = prevFilters.filter((f) => f !== "All");
                    newFilters.push(base);
                }
            }
            return newFilters;
        });
    };

    return (
        <section id="sidebarSection">
            <h3>篩選基酒</h3>
            {bases.map((base) => (
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
