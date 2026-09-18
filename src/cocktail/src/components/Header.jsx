import {Link} from "react-router-dom";

const Header = () => {
  return (
    <header>
      <section id="logo">
        <Link to="/">Last Wine</Link>
      </section>

      <nav>
        <section className="drop-down-menu">
          <div>
            <Link to="/Cocktail"><i className="fa-solid fa-wine-glass"></i> Cocktail</Link>
          </div>
          <div>
            <Link to="/AddRecipeForm"><i className="fa-solid fa-book"></i> Wine Recipe</Link>
          </div>
          <div>
            <Link to="/Admin"><i className="fa-solid fa-user-shield"></i> Admin</Link>
          </div>
        </section>
      </nav>
    </header>
  );
};

export default Header;
