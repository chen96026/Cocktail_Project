// 基酒與四維度篩選條件的單一來源，新增選項只改這裡

// 新增／編輯酒譜時可勾選的基酒
export const BASE_WINES = [
    {label: "伏特加 Vodka", value: "Vodka"},
    {label: "白蘭地 Brandy", value: "Brandy"},
    {label: "琴酒 Gin", value: "Gin"},
    {label: "威士忌 Whisky", value: "Whisky"},
    {label: "龍舌蘭 Tequila", value: "Tequila"},
    {label: "蘭姆酒 Rum", value: "Rum"},
    {label: "其他 Others", value: "Others"},
];

// 調酒列表的基酒篩選（多了 All）
export const BASE_WINE_FILTERS = ["All", ...BASE_WINES.map((wine) => wine.value)];

// 篩選器四維度，react-select 與後台 select 共用
export const MOOD_OPTIONS = ["愉悅", "普通", "煩悶", "沉重"].map((v) => ({value: v, label: v}));
export const TASTE_OPTIONS = ["酸", "甜", "苦"].map((v) => ({value: v, label: v}));
export const TONE_OPTIONS = ["冷", "暖"].map((v) => ({value: v, label: v}));
export const DRUNK_OPTIONS = ["微酣", "酩酊大醉"].map((v) => ({value: v, label: v}));
