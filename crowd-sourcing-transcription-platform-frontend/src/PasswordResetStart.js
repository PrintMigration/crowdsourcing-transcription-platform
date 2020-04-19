import React from "react";
import axios from "axios";
import { Button, Message, Input, Form } from "semantic-ui-react";

axios.defaults.baseURL = "http//10.171.204.164:9011/";
axios.defaults.headers.common["Authorization"] =
  "oZPh08pHTdb5QIsqloZin1o4xLFXA_2dbjOZZ7fHHVM";
axios.defaults.headers.post["Content-Type"] = "application/json";

export default class PasswordResetStart extends React.Component {
  state = {
    email: "",
    emailError: ""
  };

  onSubmit = e => {
    e.preventDefault();
    if (this.validation()) {
      const resetStart = {
        loginId: this.state.email
      };
      axios.post(`http//10.171.204.164:9011/api/user/forgot-password`, resetStart)
        .then( res => {
          console.log("starting password reset...");
          this.setState({email: "", emailError: ""});
        })
        .catch(error => {
          if(error.code === 404) {
            this.setState({emailError: "User not found"});
          }
        })
    }
    this.setState({
      email: ""
    });
  };

  onChange = e => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  validation = () => {
    var email = this.state.email;
    var password = this.state.password;
    this.setState({
      emailError: ""
    });
    var validForm = true;

    if (!email) {
      validForm = false;
      this.setState({
        emailError: "Please enter your email address"
      });
    } else if (typeof email !== "undefined") {
      var re = new RegExp(
        /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i
      );
      if (!re.test(email)) {
        validForm = false;
        this.setState({
          emailError: "Please enter a valid email address"
        });
      }
    }

    return validForm;
  };

  render() {
    const { email } = this.state;

    return (
      <Form onSubmit={this.onSubmit}>
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
        <Button fluid type="submit">
          Send Password Reset Request
        </Button>
      </Form>
    );
  }
}
