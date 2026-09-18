import {useState} from "react";
import {DRUNK_OPTIONS, MOOD_OPTIONS, TASTE_OPTIONS, TONE_OPTIONS} from "../constants/options.js";

const EMPTY_COMBINATION = {mood: "", taste: "", tone: "", drunk: ""};

// 四個下拉的設定，避免同一段 select 重複寫四次
const FIELDS = [
    {name: "mood", label: "心情", placeholder: "選擇心情", options: MOOD_OPTIONS},
    {name: "taste", label: "味道", placeholder: "選擇味道", options: TASTE_OPTIONS},
    {name: "tone", label: "色調", placeholder: "選擇色調", options: TONE_OPTIONS},
    {name: "drunk", label: "醉相", placeholder: "選擇醉相", options: DRUNK_OPTIONS},
];

/**
 * 後台新增四維度組合的表單
 *
 * @param onSubmit 送出處理，接到 {mood, taste, tone, drunk}
 */
const CombinationForm = ({onSubmit}) => {
    const [newCombination, setNewCombination] = useState(EMPTY_COMBINATION);

    //根據表單輸入的變化更新對象的狀態
    const handleChange = (e) => {
        const {name, value} = e.target;
        setNewCombination((prev) => ({...prev, [name]: value}));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSubmit(newCombination);
        setNewCombination(EMPTY_COMBINATION);
    };

    return (
        <section className="admin-new-combination-section">
            <h2>新增組合</h2>
            <form className="admin-new-combination-form" onSubmit={handleSubmit}>
                <section>
                    {FIELDS.map((field) => (
                        <div key={field.name}>
                            <label htmlFor={field.name}>{field.label}：</label>
                            <select
                                id={field.name}
                                name={field.name}
                                value={newCombination[field.name]}
                                onChange={handleChange}
                                required
                            >
                                <option value="" disabled>
                                    {field.placeholder}
                                </option>
                                {field.options.map((option) => (
                                    <option key={option.value} value={option.value}>{option.label}</option>
                                ))}
                            </select>
                        </div>
                    ))}
                </section>
                <button type="submit">新增組合</button>
            </form>
        </section>
    );
};

export default CombinationForm;
