import globals from "globals";
import pluginJs from "@eslint/js";
import pluginReact from "eslint-plugin-react";


/** @type {import('eslint').Linter.Config[]} */
export default [
  {files: ["**/*.{js,mjs,cjs,jsx}"]},
  {languageOptions: { globals: globals.browser },
  rules: {
       "react/react-in-jsx-scope": "off", // 禁用 React 必須引入的規則
       "react/prop-types": "off", // 禁用 PropTypes 檢查
       "no-unused-vars": ["warn", { varsIgnorePattern: "^_" }], // 忽略未使用變數
     },
  },
  pluginJs.configs.recommended,
  pluginReact.configs.flat.recommended,
];