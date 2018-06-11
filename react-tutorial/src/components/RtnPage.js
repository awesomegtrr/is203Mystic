import React, { Component } from 'react';

class RtnPage extends Component {
  render() {
    const {app_name} = this.props;
    return(
      <h1 className="display-1">{app_name}</h1>
    );
  }

}

export default RtnPage;
