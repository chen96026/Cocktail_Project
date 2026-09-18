// react-select 的樣式覆寫，供篩選器的四個下拉共用
export const selectStyles = {
    control: (provided) => ({
        ...provided,
        backgroundColor: "#f9f9f9",//淺灰色
        borderRadius: "8px",
        border: "none",
        padding: "1vh",
        boxShadow: "none",
        cursor: "pointer",
        "&:hover": {
            borderColor: "black",
        },
    }),
    menu: (provided) => ({
        ...provided,
        borderRadius: "8px",
        zIndex: 10,
    }),
    option: (provided, state) => ({
        ...provided,
        backgroundColor: state.isFocused ? "black" : "#fff",
        color: state.isFocused ? "#fff" : "#333",
        cursor: "pointer",
        "&:active": {
            backgroundColor: "black",
            color: "#fff",
        },
    }),
    placeholder: (provided) => ({
        ...provided,
        color: "#999",
        fontSize: "1vw",
    }),
};
