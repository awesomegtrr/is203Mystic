import React, { Component } from 'react';
import { NavLink } from 'react-router-dom';

class Header extends Component {

  render() {
    return (
      <nav>
        <NavLink exact to="/" activeClassName="active">Home</NavLink>
        <NavLink to="/about" activeClassName="active">About</NavLink>
      </nav>
    );
  }
}

export default Header;
