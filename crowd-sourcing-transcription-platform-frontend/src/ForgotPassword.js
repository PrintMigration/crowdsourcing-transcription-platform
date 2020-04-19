import React from "react";
import axios from "axios";

export default class ForgotPassword extends React.Component {
  state = {
    email: "",
    emailError: "",
    authError: ""
  };

  onSubmit = e => {
    e.preventDefault();
    if (this.validation()) {
      const resetStart = {
        loginId: this.state.email
      };
      axios
        .post(`http://10.171.204.164:9011/api/user/forgot-password`, resetStart)
        .then();
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
    console.log("We enter validation");
    var email = this.state.email;
    this.setState({
      emailError: "",
      authError: ""
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
          <div class="ui negative message">{this.state.emailError}</div>
        ) : (
          ""
        )}
      </Form.Field>
      <Button>Password Reset Request</Button>
    </Form>;
  }
}
