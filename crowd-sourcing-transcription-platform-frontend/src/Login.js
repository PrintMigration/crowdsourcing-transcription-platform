import React from "react";
import axios from "axios";
import { Button, Input, Form, Message } from "semantic-ui-react";

axios.defaults.baseURL = "http//10.171.204.164:9011/";
axios.defaults.headers.common["Authorization"] =
  "oZPh08pHTdb5QIsqloZin1o4xLFXA_2dbjOZZ7fHHVM";
axios.defaults.headers.post["Content-Type"] = "application/json";

class Login extends React.Component {
  state = {
    email: "",
    password: "",
    emailError: "",
    passwordError: "",
    authError: "",
  };

  onSubmit = (e) => {
    e.preventDefault();
    if (this.validation()) {
      const login = {
        loginId: this.state.email,
        password: this.state.password,
      };
      //console.log(JSON.stringify(login));
      axios
        .post("http://10.171.204.164:8080/fusionauth/login", login)
        .then((res) => {
          //console.log(res);
          this.props.handleLogIn(res.data.email);
        })
        .catch((error) => {
          if (error.code === 404) {
            this.setState({ authError: "Incorrect Username or Password" });
          }
        });
    }
  };

  onChange = (e) => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  validation = () => {
    var email = this.state.email;
    var password = this.state.password;
    this.setState({
      emailError: "",
      passwordError: "",
      authError: "",
    });
    var validForm = true;

    if (!email) {
      validForm = false;
      this.setState({
        emailError: "Please enter your email address",
      });
    } else if (typeof email !== "undefined") {
      var re = new RegExp(
        /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i
      );
      if (!re.test(email)) {
        validForm = false;
        this.setState({
          emailError: "Please enter a valid email address",
        });
      }
    }

    if (!password) {
      validForm = false;
      this.setState({
        passwordError: "Please enter your password",
      });
    }

    return validForm;
  };

  render() {
    const { email, password } = this.state;

    return (
      <Form onSubmit={this.onSubmit}>
        {this.state.authError ? (
          <Message negative>{this.state.authError}</Message>
        ) : (
          ""
        )}
        <Form.Field>
          <label>Email</label>
          <Input
            name="email"
            onChange={this.onChange}
            value={email}
            placeholder="Email"
            fluid
          />
          {this.state.emailError ? (
            <Message negative>{this.state.emailError}</Message>
          ) : (
            ""
          )}
        </Form.Field>
        <Form.Field>
          <label>Password</label>
          <Input
            name="password"
            onChange={this.onChange}
            value={password}
            type="password"
            placeholder="Password"
            fluid
          />
          {this.state.passwordError ? (
            <Message negative>{this.state.passwordError}</Message>
          ) : (
            ""
          )}
        </Form.Field>
        <Button fluid type="submit">
          Sign In
        </Button>
      </Form>
    );
  }
}

export default Login;
