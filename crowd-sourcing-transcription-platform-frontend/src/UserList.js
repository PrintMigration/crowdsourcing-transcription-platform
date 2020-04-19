import React from "react";
import { Form, Accordion, Icon, List, Button, Modal } from "semantic-ui-react";
import Axios from "axios";

const roleList = [
  {
    key: "Transcriber",
    text: "Transcriber",
    value: "Transcriber",
  },
  {
    key: "Editor",
    text: "Editor",
    value: "Editor",
  },
  {
    key: "Encoder",
    text: "Encoder",
    value: "Encoder",
  },
  {
    key: "Metadata Expert",
    text: "Metadata Expert",
    value: "Metadata Expert",
  },
];

class UserList extends React.Component {
  state = {
    open: null,

    pageNum: 0,
    pageSize: 10,
    totalPages: 1,

    activeIndex: -1,
    searchEmail: "",
    searchRole: "",
    users: [],
    roles: [],
  };

  componentWillMount = () => {
    const roles = [];
    Axios.get(
      `http://10.171.204.164:8080/fusionauth/users?page=${this.state.pageNum}`,
      {
        withCredentials: true,
      }
    )
      .then((response) => {
        if (response.data.length > 0) {
          response.data.map((user) => {
            if (user.registrations[0].roles.indexOf("Administrator") > -1) {
              roles.push("Administrator");
            } else if (
              user.registrations[0].roles.indexOf("Metadata Expert") > -1
            ) {
              roles.push("Metadata Expert");
            } else if (user.registrations[0].roles.indexOf("Encoder") > -1) {
              roles.push("Encoder");
            } else if (user.registrations[0].roles.indexOf("Editor") > -1) {
              roles.push("Editor");
            } else {
              roles.push("Transcriber");
            }
          });
        }
        this.setState({ users: response.data, roles });
      })
      .catch((error) => console.log(error.response.data));
  };

  openModal = (id) => this.setState({ open: id });

  close = (id) => this.setState({ open: null });

  handlePrevPage = () => {
    if (this.state.pageNum > 0) {
      const pageNum = this.state.pageNum - 1;
      this.setState({ pageNum }, () => {
        this.handleUpdate();
      });
    }
  };

  handleNextPage = () => {
    console.log(this.state.pageNum);
    console.log(this.state.totalPages);
    if (this.state.pageNum < this.state.totalPages - 1) {
      const pageNum = this.state.pageNum + 1;
      this.setState({ pageNum }, () => {
        this.handleUpdate();
      });
    }
  };

  handleAccordion = (e, titleProps) => {
    const { index } = titleProps;
    const { activeIndex } = this.state;
    const newIndex = activeIndex === index ? -1 : index;

    this.setState({ activeIndex: newIndex });
  };

  onChange = (e) => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  onChangeSearchRole = (e, data) => {
    this.setState({ searchRole: data.value });
  };

  onChangeRole = (index, event) => {
    const roles = [...this.state.roles];
    roles[index] = event.target.textContent;
    this.setState({ roles });
  };

  onSubmitSearch = () => {
    const roles = [];
    Axios.get(
      `http://10.171.204.164:8080/fusionauth/users?page=${this.state.pageNum}${
        this.state.searchEmail && `&email=${this.state.searchEmail}`
      }${this.state.searchRole && `&role=${this.state.searchRole}`}`,
      {
        withCredentials: true,
      }
    ).then((response) => {
      if (response.data.length > 0) {
        response.data.map((user) => {
          if (user.registrations[0].roles.indexOf("Administrator") > -1) {
            roles.push("Administrator");
          } else if (
            user.registrations[0].roles.indexOf("Metadata Expert") > -1
          ) {
            roles.push("Metadata Expert");
          } else if (user.registrations[0].roles.indexOf("Encoder") > -1) {
            roles.push("Encoder");
          } else if (user.registrations[0].roles.indexOf("Editor") > -1) {
            roles.push("Editor");
          } else {
            roles.push("Transcriber");
          }
        });
      }
      this.setState({ users: response.data, roles, pageNum: 0 });
    });
  };

  handleRoleSubmit = (role, userID) => {
    const assignRole = {
      role,
      userID,
    };
    Axios.post(`http://10.171.204.164:8080/roles/assign`, assignRole).then(
      () => {
        console.log(`${role} asigned to ${userID}`);
      }
    );
  };

  handleDeactivateConfirm = (user, index) => {
    console.log(user.email);
    Axios.delete(`http://10.171.204.164:8080/deactivate?userID=${user.email}`)
      .then((response) => {
        console.log(response);
      })
      .catch((error) => console.log(error));
    this.setState({ open: null });
  };

  render() {
    const { activeIndex, roles, searchEmail, searchRole, open } = this.state;
    const SearchSelectRole = [
      {
        key: "Any Role",
        text: "Any Role",
        value: "",
      },
    ].concat(
      roleList.concat({
        key: "Administrator",
        text: "Administrator",
        value: "Administrator",
      })
    );
    return (
      <div>
        <h1>User List</h1>
        <Form onSubmit={() => this.onSubmitSearch()}>
          <Form.Group widths="equal">
            <Form.Input
              name="searchEmail"
              onChange={this.onChange}
              value={searchEmail}
              placeholder="Search by Email"
              fluid
            />
            <Form.Select
              options={SearchSelectRole}
              value={searchRole}
              placeholder="Any Role"
              onChange={this.onChangeSearchRole}
              fluid
            />
            <Form.Button type="submit">Search</Form.Button>
          </Form.Group>
        </Form>
        <Accordion styled fluid>
          {this.state.users.map((user, index) => {
            return (
              <div key={index}>
                <Accordion.Title
                  active={activeIndex === index}
                  index={index}
                  onClick={this.handleAccordion}
                >
                  <Icon name="dropdown" />
                  Username:{" "}
                  {user.registrations[0].username
                    ? user.registrations[0].username
                    : user.username
                    ? user.username
                    : "Undefined"}
                  , email:
                  {"  "}
                  {user.email ? user.email : "Undefined"}
                </Accordion.Title>
                <Accordion.Content active={activeIndex === index}>
                  <List>
                    <List.Item>
                      First Name:{" "}
                      {user.firstName ? user.firstName : "Undefined"}
                    </List.Item>
                    <List.Item>
                      Last Name: {user.lastName ? user.lastName : "Undefined"}
                    </List.Item>
                    <List.Item>
                      City:{" "}
                      {user.registrations[0].data.city
                        ? user.registrations[0].data.city
                        : "Undefined"}
                    </List.Item>
                    <List.Item>
                      Country:{" "}
                      {user.registrations[0].data.country
                        ? user.registrations[0].data.country
                        : "Undefined"}
                    </List.Item>
                    <List.Item>
                      State or Province:{" "}
                      {user.registrations[0].data.province
                        ? user.registrations[0].data.province
                        : "Undefined"}
                    </List.Item>
                  </List>
                  <Form>
                    {roles[index] === "Administrator" ? (
                      roles[index]
                    ) : (
                      <Form.Group>
                        <Form.Select
                          options={roleList}
                          value={roles[index]}
                          onChange={(event) => this.onChangeRole(index, event)}
                        />
                        <Form.Button
                          onClick={() =>
                            this.handleRoleSubmit(roles[index], user.email)
                          }
                        >
                          Save Changes
                        </Form.Button>
                      </Form.Group>
                    )}
                  </Form>
                  {roles[index] !== "Administrator" && (
                    <Modal
                      dimmer={"blurring"}
                      open={open === index}
                      trigger={
                        <Button onClick={() => this.openModal(index)} negative>
                          Deactivate User
                        </Button>
                      }
                      onClose={this.close}
                    >
                      <Modal.Header>Deactivate User {index}</Modal.Header>
                      <Modal.Content>
                        <p>
                          Warning: Continuing will deactivate the account of{" "}
                          {user.email}
                        </p>
                      </Modal.Content>
                      <Modal.Actions>
                        <Button onClick={this.close}>Cancel</Button>
                        <Button
                          onClick={() =>
                            this.handleDeactivateConfirm(user, index)
                          }
                          negative
                        >
                          DEACTIVATE
                        </Button>
                      </Modal.Actions>
                    </Modal>
                  )}
                </Accordion.Content>
              </div>
            );
          })}
        </Accordion>
      </div>
    );
  }
}

export default UserList;
