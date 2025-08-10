import React from "react";
import {Link} from "react-router-dom";

const BaseWineSection = ({baseWines}) => {
    return (
        <main id="allContent">
            <h3 id="topTitle">六大基酒</h3>
            <section id="info-circles">
                <div id="basewine_bigcircle">
                    {baseWines.map((wine, index) => (
                        <div key={index} className="basewine_smallcircle">
                            <div className="basewine_button">
                                {/*對應Home.js*/}
                                <Link to={wine.path}>{wine.name}</Link>
                            </div>
                        </div>
                    ))}
                </div>
            </section>
        </main>
    );
};

export default BaseWineSection;
