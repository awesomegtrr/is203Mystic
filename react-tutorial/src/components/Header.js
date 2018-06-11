import React, { Component } from 'react';
import { NavLink, Link } from 'react-router-dom';

class Header extends Component {

  render() {
    return (
      <nav>
        <NavLink exact to="/" activeClassName="active">Home</NavLink>
        <NavLink to="/about" activeClassName="active">About</NavLink>
        <Link to="/about/1">1</Link>
      </nav>
    );
  }
}

export default Header;
