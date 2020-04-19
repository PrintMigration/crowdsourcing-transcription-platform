import React from 'react';
import { Form } from 'semantic-ui-react';

export default class AddPersonSearch extends React.Component {
  state = {
    people: [],
    firstName: '',
    middleName: '',
    lastName: '',
    roleDesc: '',
  };

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value }, () => {
      this.props.handleUpdate(
        [
          {
            person: {
              biography: '',
              firstName: this.state.firstName,
              lastName: this.state.lastName,
              middleName: this.state.middleName,
              occupation: '',
              personLOD: '',
              prefix: '',
              suffix: '',
            },
            role: {
              roleDesc: this.state.roleDesc,
            },
          },
        ].concat(this.state.people)
      );
    });
  };

  handleEdit = (index, event) => {
    const people = [...this.state.people];
    if (
      event.target.name === 'firstName' ||
      event.target.name === 'middleName' ||
      event.target.name === 'lastName'
    ) {
      people[index].person[event.target.name] = event.target.value;
    } else if (event.target.name === 'roleDesc') {
      people[index].role[event.target.name] = event.target.value;
    }
    this.setState({ people }, () => {
      this.props.handleUpdate(
        [
          {
            person: {
              biography: '',
              firstName: this.state.firstName,
              lastName: this.state.lastName,
              middleName: this.state.middleName,
              occupation: '',
              personLOD: '',
              prefix: '',
              suffix: '',
            },
            role: {
              roleDesc: this.state.roleDesc,
            },
          },
        ].concat(this.state.people)
      );
    });
  };

  handleAdd = () => {
    this.setState(
      {
        people: [
          ...this.state.people,
          {
            person: {
              biography: '',
              firstName: this.state.firstName,
              lastName: this.state.lastName,
              middleName: this.state.middleName,
              occupation: '',
              personLOD: '',
              prefix: '',
              suffix: '',
            },
            role: {
              roleDesc: this.state.roleDesc,
            },
          },
        ],
        firstName: '',
        middleName: '',
        lastName: '',
        roleDesc: '',
      },
      () => {
        this.props.handleUpdate(
          [
            {
              person: {
                biography: '',
                firstName: this.state.firstName,
                lastName: this.state.lastName,
                middleName: this.state.middleName,
                occupation: '',
                personLOD: '',
                prefix: '',
                suffix: '',
              },
              role: {
                roleDesc: this.state.roleDesc,
              },
            },
          ].concat(this.state.people)
        );
      }
    );
  };

  handleDelete = (index) => {
    this.state.people.splice(index, 1);
    this.setState({ people: this.state.people }, () => {
      this.props.handleUpdate(
        [
          {
            person: {
              biography: '',
              firstName: this.state.firstName,
              lastName: this.state.lastName,
              middleName: this.state.middleName,
              occupation: '',
              personLOD: '',
              prefix: '',
              suffix: '',
            },
            role: {
              roleDesc: this.state.roleDesc,
            },
          },
        ].concat(this.state.people)
      );
    });
  };

  render() {
    let view;
    if (this.props.pushed) {
      view = (
        <div>
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='firstName'
            label='First Name'
            placeholder='First Name'
            value={this.state.firstName}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='middleName'
            label='Middle Name'
            placeholder='Middle Name'
            value={this.state.middleName}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='lastName'
            label='Last Name'
            placeholder='Last Name'
            value={this.state.lastName}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='roleDesc'
            label='Role'
            placeholder='Role'
            value={this.state.roleDesc}
          />
          <Form.Button fluid icon='add' onClick={this.handleAdd} />
          {this.state.people.map((person, index) => {
            return (
              <div key={index}>
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='firstName'
                  label='First Name'
                  value={person.person.firstName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='middleName'
                  label='Middle Name'
                  value={person.person.middleName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='lastName'
                  label='Last Name'
                  value={person.person.lastName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='roleDesc'
                  label='Role'
                  value={person.role.roleDesc}
                />
                <Form.Button
                  fluid
                  icon='minus'
                  onClick={() => {
                    this.handleDelete(index);
                  }}
                />
              </div>
            );
          })}
        </div>
      );
    } else {
      view = (
        <div>
          <Form.Group>
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='firstName'
              label='First Name'
              placeholder='First Name'
              value={this.state.firstName}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='middleName'
              label='Middle Name'
              placeholder='Middle Name'
              value={this.state.middleName}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='lastName'
              label='Last Name'
              placeholder='Last Name'
              value={this.state.lastName}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='roleDesc'
              label='Role'
              placeholder='Role'
              value={this.state.roleDesc}
            />
            <Form.Button
              type='button'
              style={{ margin: '27px 0px 0px 0px' }}
              icon='add'
              circular
              onClick={this.handleAdd}
            />
          </Form.Group>
          {this.state.people.map((person, index) => {
            return (
              <Form.Group key={index}>
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='firstName'
                  label='First Name'
                  value={person.person.firstName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='middleName'
                  label='Middle Name'
                  value={person.person.middleName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='lastName'
                  label='Last Name'
                  value={person.person.lastName}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='roleDesc'
                  label='Role'
                  value={person.role.roleDesc}
                />
                <Form.Button
                  type='button'
                  style={{ margin: '27px 0px 0px 0px' }}
                  icon='minus'
                  circular
                  onClick={() => {
                    this.handleDelete(index);
                  }}
                />
              </Form.Group>
            );
          })}
        </div>
      );
    }
    return <div>{view}</div>;
  }
}
