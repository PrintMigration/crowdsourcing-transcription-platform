import React from "react";
import axios from "axios";
import { Button, Input, Form, Message } from "semantic-ui-react";

class Register extends React.Component {
  state = {
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    country: "",
    province: "",
    city: "",
    errors: {},
  };

  onSubmit = () => {
    if (this.validation()) {
      const registration = {
        registration: {
          username: this.state.username,
          roles: ["Transcriber"],
          data: {
            country: this.state.country,
            province: this.state.province,
            city: this.state.city,
          },
        },
        user: {
          email: this.state.email,
          password: this.state.password,
          firstName: this.state.firstName,
          lastName: this.state.lastName,
        },
      };
      console.log(JSON.stringify(registration));
      axios
        .post(
          "http://10.171.204.164:8080/fusionauth/user/register",
          registration
        )
        .then((res) => {
          console.log(res);
          window.location.reload();
        })
        .catch((error) => {
          console.log(error);
        });
    }
  };

  onChange = (e) => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  validation = () => {
    var firstName = this.state.firstName;
    var lastName = this.state.lastName;
    var email = this.state.email;
    var password = this.state.password;
    var country = this.state.country;
    var province = this.state.province;
    var city = this.state.city;
    var errors = {};
    var validForm = true;

    if (!firstName) {
      validForm = false;
      errors["firstName"] = "Please enter your first name.";
    }

    if (!lastName) {
      validForm = false;
      errors["lastName"] = "Please enter your last name.";
    }

    if (!email) {
      validForm = false;
      errors["email"] = "Please enter your email address.";
    } else if (typeof email !== "undefined") {
      var re = new RegExp(
        /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i
      );
      if (!re.test(email)) {
        validForm = false;
        errors["email"] = "Please enter a valid email address.";
      }
    }

    if (!password) {
      validForm = false;
      errors["password"] = "Please enter a password.";
    } else if (typeof password !== "undefined") {
      if (
        !password.match(
          /^.*(?=.{8,})(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%&*?]).*$/
        )
      ) {
        validForm = false;
        errors["password"] = "Please use a stronger password.";
      }
    }

    if (!country) {
      validForm = false;
      errors["country"] = "Please enter your country.";
    }
    if (!province) {
      validForm = false;
      errors["province"] = "Please enter your state or province.";
    }
    if (!city) {
      validForm = false;
      errors["city"] = "Please enter your city.";
    }

    this.setState({
      errors: errors,
    });

    return validForm;
  };

  render() {
    const {
      username,
      firstName,
      lastName,
      email,
      password,
      country,
      province,
      city,
    } = this.state;

    return (
      <Form onSubmit={() => this.onSubmit()}>
        <Form.Field>
          <label>Username</label>
          <Input
            name="username"
            onChange={this.onChange}
            value={username}
            placeholder="Username"
            fluid
          />
        </Form.Field>
        <Form.Field>
          <label>Email</label>
          <Input
            name="email"
            onChange={this.onChange}
            value={email}
            placeholder="Email"
            fluid
          />
          {this.state.errors.email ? (
            <Message negative>{this.state.errors.email}</Message>
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
          <div>
            8 characters minimum, 1 lowercase letter, 1 uppercase letter 1
            number, and at least one of the following: !@#$%&*?
          </div>
          {this.state.errors.password ? (
            <Message negative>{this.state.errors.password}</Message>
          ) : (
            ""
          )}
        </Form.Field>

        <Form.Group widths="equal">
          <Form.Field>
            <label>First Name</label>
            <Input
              name="firstName"
              onChange={this.onChange}
              value={firstName}
              placeholder="First Name"
              fluid
            />
            {this.state.errors.firstName ? (
              <Message negative>{this.state.errors.firstName}</Message>
            ) : (
              ""
            )}
          </Form.Field>
          <Form.Field>
            <label>Last Name</label>
            <Input
              name="lastName"
              onChange={this.onChange}
              value={lastName}
              placeholder="Last Name"
              fluid
            />
            {this.state.errors.lastName ? (
              <Message negative>{this.state.errors.lastName}</Message>
            ) : (
              ""
            )}
          </Form.Field>
        </Form.Group>
        <Form.Field>
          <label>City</label>
          <Input
            name="city"
            onChange={this.onChange}
            value={city}
            placeholder="City"
            fluid
          />
          {this.state.errors.city ? (
            <Message negative>{this.state.errors.city}</Message>
          ) : (
            ""
          )}
        </Form.Field>
        <Form.Group widths="equal">
          <Form.Field>
            <label>State or Province</label>
            <Input
              name="province"
              onChange={this.onChange}
              value={province}
              placeholder="State or Province"
              fluid
            />
            {this.state.errors.province ? (
              <Message negative>{this.state.errors.province}</Message>
            ) : (
              ""
            )}
          </Form.Field>
          <Form.Field>
            <label>Country </label>
            <Input
              name="country"
              onChange={this.onChange}
              value={country}
              placeholder="Country"
              fluid
            />
            {this.state.errors.country ? (
              <Message negative>{this.state.errors.country}</Message>
            ) : (
              ""
            )}
          </Form.Field>
        </Form.Group>
        <Button fluid type="submit">
          Register
        </Button>
      </Form>
    );
  }
}

export default Register;
