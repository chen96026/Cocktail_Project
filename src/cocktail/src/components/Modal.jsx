import React from "react";

const Modal = ({ isOpen, onClose, children }) => {
    if (!isOpen) return null; // 如果isOpen為false，不渲染內容

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-body">{children}</button>
            </div>
        </div>
    );
};

export default Modal;
