import React from "react";

const Footer = () => {
  return (
    <footer>
      <section id="big_footer">
        <section className="pic1">
          <div id="lastTitle">Last Wine</div>
        </section>
        <section className="pic2">
          <form action="">
            <div id="miss">
              <p>Don't miss the fun</p>
            </div>
            <br />
          </form>
        </section>
      </section>
      <div id="backgroundslogan">
        <div><p>嚴 禁 酒 駕</p></div>
        &nbsp;
        <div><i className="fa-solid fa-ban"></i> </div>
        &nbsp;
        <div><p>適 度 飲 酒 有 助 健 康</p></div>
      </div>
    </footer>
  )
}

export default Footer;