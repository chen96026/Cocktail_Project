import Select from "react-select";
import {DRUNK_OPTIONS, MOOD_OPTIONS, TASTE_OPTIONS, TONE_OPTIONS} from "../constants/options.js";
import {selectStyles} from "../constants/selectStyles.js";

// 四個下拉的設定，避免同一段 Select 重複寫四次
const FIELDS = [
    {key: "mood", id: "feel", label: "心情", placeholder: "請選擇心情", options: MOOD_OPTIONS},
    {key: "taste", id: "taste", label: "味道", placeholder: "請選擇味道", options: TASTE_OPTIONS},
    {key: "tone", id: "tone", label: "色調", placeholder: "請選擇色調", options: TONE_OPTIONS},
    {key: "drunk", id: "drunk", label: "醉相", placeholder: "請選擇醉相", options: DRUNK_OPTIONS},
];

/**
 * 篩選器的四個條件下拉
 *
 * @param selection 目前選到的條件 {mood, taste, tone, drunk}
 * @param onChange  單一條件變更，接到 (key, option)
 * @param onSubmit  送出查詢
 * @param onClose   關閉表單
 */
const SelectorForm = ({selection, onChange, onSubmit, onClose}) => {
    return (
        <section id="background-sqareForm">
            <div className="closeButton"><i onClick={onClose} className="fa-solid fa-xmark"></i></div>
            <section id="background-sqareTop">
                {FIELDS.map((field) => (
                    <div key={field.key} id={field.id}>
                        <p>{field.label}</p>
                        {/*這邊使用套件*/}
                        <Select
                            options={field.options}
                            value={selection[field.key]}
                            onChange={(option) => onChange(field.key, option)}
                            styles={selectStyles}
                            placeholder={field.placeholder}
                        />
                    </div>
                ))}
            </section>
            <button onClick={onSubmit} className="cocktail_button" id="submit_button">Submit</button>
        </section>
    );
};

export default SelectorForm;
