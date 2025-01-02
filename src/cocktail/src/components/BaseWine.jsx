import React from "react";

const BaseWine = ({title, description, imagePath}) => {
    return (
        <section className="basewine-container">
            <section className="basewine-background-img"></section>
            <h3 className="basewineTitle">{title}</h3>
            <div className="basewineDescription">
                {description.map((line, index) => (
                    <React.Fragment key={index}>
                        {line}
                        <br/>
                    </React.Fragment>
                ))}
            </div>
            <section>
                <img className="introducePic" src={imagePath} alt={title}/>
            </section>
        </section>
    );
};

export default BaseWine;
