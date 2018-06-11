import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

const propTypes = {
  name: PropTypes.string.isRequired,
  onClicked: PropTypes.func,
  title: PropTypes.any
};

const defaultProps = {
  name: 'Default name of App'
}

class MyComponent extends Component {
  constructor(props) {
    super(props);
    this.buttonHandler = this.buttonHandler.bind(this);
    this.state = {
      callBack: null
    };
  }
  componentWillMount() {
    console.log('WILL MOUNT');
  }

  componentDidMount() {
    console.log('DID MOUNT');
  }

  buttonHandler(event) {
    console.log('buttonhanlder called');
    this.props.callBackFromParent(event.target.getAttribute('value'));
  }

  render() {
    const { title, name, onClick } = this.props;
    return (
      <div className="component">
        <h1>Title: {title}</h1>
        <h2>Name: {name}</h2>
        <div onClick={onClick}>Click me</div>
        <Link to="/about/1" onClick={this.buttonHandler} value={'CORE'}>CORE</Link>
      </div>
    );
  }
}

MyComponent.propTypes = propTypes;
MyComponent.defaultProps = defaultProps;

export default MyComponent;
