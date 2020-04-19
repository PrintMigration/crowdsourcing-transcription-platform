import React from 'react';
import { Form } from 'semantic-ui-react';

export default class AddOrganizationSearch extends React.Component {
  state = { organizations: [], orgName: '' };

  handleChange = (e, { name, value }) =>
    this.setState({ [name]: value }, () => {
      this.props.handleUpdate(
        [
          {
            org: { orgLOD: '', orgName: this.state.orgName },
            role: { roleDesc: '' },
          },
        ].concat(this.state.organizations)
      );
    });

  handleEdit = (index, event) => {
    const organizations = [...this.state.organizations];
    organizations[index].org[event.target.name] = event.target.value;
    this.setState({ organizations }, () => {
      this.props.handleUpdate(
        [
          {
            org: { orgLOD: '', orgName: this.state.orgName },
            role: { roleDesc: '' },
          },
        ].concat(this.state.organizations)
      );
    });
  };

  handleAdd = () => {
    this.setState(
      {
        organizations: [
          ...this.state.organizations,
          {
            org: { orgLOD: '', orgName: this.state.orgName },
            role: { roleDesc: '' },
          },
        ],
        orgName: '',
      },
      () => {
        this.props.handleUpdate(
          [
            {
              org: { orgLOD: '', orgName: this.state.orgName },
              role: { roleDesc: '' },
            },
          ].concat(this.state.organizations)
        );
      }
    );
  };

  handleDelete = (index) => {
    this.state.organizations.splice(index, 1);
    this.setState({ organizations: this.state.organizations }, () => {
      this.props.handleUpdate(
        [
          {
            org: { orgLOD: '', orgName: this.state.orgName },
            role: { roleDesc: '' },
          },
        ].concat(this.state.organizations)
      );
    });
  };

  render() {
    let buttonStyle;
    if (this.props.pushed) {
      buttonStyle = { margin: '23px 0px 0px 0px' };
    } else {
      buttonStyle = { margin: '27px 0px 0px 0px' };
    }
    return (
      <div>
        <Form.Group>
          <Form.Input
            onChange={this.handleChange}
            width={16}
            fluid
            name='orgName'
            label='Organization'
            placeholder='Organization'
            value={this.state.orgName}
          />
          <Form.Button
            type='button'
            style={buttonStyle}
            icon='add'
            circular
            onClick={this.handleAdd}
          />
        </Form.Group>
        {this.state.organizations.map((orgName, index) => {
          return (
            <Form.Group key={index}>
              <Form.Input
                onChange={(event) => this.handleEdit(index, event)}
                width={16}
                fluid
                name='orgName'
                label='Organization'
                value={orgName.org.orgName}
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
}
