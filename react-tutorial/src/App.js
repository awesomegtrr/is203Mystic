import React, { Component } from 'react';
import MyComponent from './MyComponent';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import './App.css';
import Header from './components/Header';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      title: 'Initial name',
      name: 'Initial name',
      shouldRenderTitle: true
    };
    // required to bind if method is accessing props/state within component
    this.onSubmit = this.onSubmit.bind(this);
    this.onClick = this.onClick.bind(this);
    this.updateName = this.updateName.bind(this);
  }

  renderTitle() {
    if (!this.state.shouldRenderTitle) { return null; }

    return <h1>Title</h1>;
  }

  onSubmit(event) {
    event.preventDefault();
    console.log(this.input.value);
  }

  onClick() {
    this.setState({
      name: 'New app name',
      // title: 'New app title'
    });
  }

  updateName(event) {
    this.setState({
      name: event.target.value
    });
  }

  render() {
    // const list = [
    //   'Item 1',
    //   'Item 2',
    //   'Another Item'
    // ];

    return (
      <Router>
        <div className="App">
          <Header />
          <Switch>
            <Route exact path="/" component={MyComponent} />
            <Switch>
              <Route path="/about" component={MyComponent} />
              <Route path="/about/:id" component={MyComponent} />
            </Switch>
          </Switch>

          {this.renderTitle()}
          {/* <h1>
          {this.state.title} */}
          {/* {
            list.map( item => {
              return (
                <div key={item} onMouseEnter={this.onMouseEnter}>{item}</div>
              );
            })
          } */}
          {/* </h1>
        <div onClick={this.onClick}>Click here!</div> */}
          <input
            value={this.state.name}
            onChange={this.updateName}
          />
          <MyComponent
            name={this.state.name}
            title={this.state.title}
            onClick={this.onClick}
          />
          {/* <form onSubmit={this.onSubmit}>
          <input onChange={this.onChange} ref={input => this.input = input}/>
        </form> */}
        </div>
      </Router>
    );
  }
}

export default App;
