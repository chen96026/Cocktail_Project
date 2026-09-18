import globals from "globals";
import pluginJs from "@eslint/js";
import pluginReact from "eslint-plugin-react";
import pluginReactHooks from "eslint-plugin-react-hooks";

/** @type {import('eslint').Linter.Config[]} */
export default [
    {ignores: ["dist/**", "node_modules/**"]},
    {files: ["**/*.{js,mjs,cjs,jsx}"]},
    {languageOptions: {globals: globals.browser}},
    pluginJs.configs.recommended,
    pluginReact.configs.flat.recommended,
    // 新版 JSX transform，檔案不需要自己 import React
    pluginReact.configs.flat["jsx-runtime"],
    // 覆寫必須放在 recommended 之後，否則會被蓋回去
    {
        plugins: {"react-hooks": pluginReactHooks},
        settings: {react: {version: "detect"}},
        rules: {
            ...pluginReactHooks.configs.recommended.rules,
            "react/prop-types": "off", // 專案不使用 PropTypes
            "no-unused-vars": ["warn", {varsIgnorePattern: "^_", argsIgnorePattern: "^_"}],
        },
    },
];
