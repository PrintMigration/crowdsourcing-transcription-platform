import React from "react";
import Axios from "axios";
import {
  Icon,
  Accordion,
  Grid,
  Divider,
  Sticky,
  Button,
  List,
  Input,
  Modal,
  Form,
} from "semantic-ui-react";
import { Link } from "react-router-dom";

import Notifications from "./Notifications";
import Requests from "./Requests";

// Get rid of user description. Add ability to change other user data such as country.
// when no requests, give message rather than saying 1/0 pages

class UserPage extends React.Component {
  // documents being an array of the users current projects
  state = {
    open: null,
    activeIndex: -1,

    userID: null,
    documents: [],
    notifications: [],
    settings: false,

    username: "",
    firstName: "",
    lastName: "",
    city: "",
    province: "",
    country: "",

    changeUsername: "",
    changeFirst: "",
    changeLast: "",
    changeCity: "",
    changeProvince: "",
    changeCountry: "",
    userData: {},
    admin: false,
    expert: false,
    encoder: false,
    editor: false,
    roleText: "Transcriber",
    role: [],
    errors: {},
  };

  openModal = (id) => this.setState({ open: id });

  close = () => this.setState({ open: null });

  componentDidMount = () => {
    // place documents tied to the user in this.state.documents
    Axios.get(`http://10.171.204.164:8080/fusionauth/user`, {
      withCredentials: true,
    })
      .then((response) => {
        console.log(response);
        this.setState(
          {
            userID: response.data.email,
            userData: response.data,
            role: response.data.registrations[0].roles,
          },
          () => {
            if (this.state.role.includes("Administrator")) {
              this.setState({
                admin: true,
                expert: true,
                encoder: true,
                editor: true,
                roleText: "Administrator",
              });
            } else if (this.state.role.includes("Metadata Expert")) {
              this.setState({
                expert: true,
                encoder: true,
                editor: true,
                roleText: "Metadata Expert",
              });
            } else if (this.state.role.includes("Encoder")) {
              this.setState({
                encoder: true,
                editor: true,
                roleText: "Encoder",
              });
            } else if (this.state.role.includes("Editor")) {
              this.setState({ editor: true, roleText: "Editor" });
            }
            this.setState({
              username: this.state.userData.registrations[0].username || "",
              changeUsername:
                this.state.userData.registrations[0].username || "",
              firstName: this.state.userData.firstName || "",
              changeFirst: this.state.userData.firstName || "",
              lastName: this.state.userData.lastName || "",
              changeLast: this.state.userData.lastName || "",
              city: this.state.userData.registrations[0].data.city || "",
              changeCity: this.state.userData.registrations[0].data.city || "",
              province:
                this.state.userData.registrations[0].data.province || "",
              changeProvince:
                this.state.userData.registrations[0].data.province || "",
              country: this.state.userData.registrations[0].data.country || "",
              changeCountry:
                this.state.userData.registrations[0].data.country || "",
            });
          }
        );
      })
      .then(() => {
        Axios.get(`http://10.171.204.164:8080/edits/${this.state.userID}`).then(
          (response) => {
            this.setState({ documents: response.data });
          }
        );
      })
      .catch((error) => console.log(error.response.data));
  };

  handleAccordion = (e, titleProps) => {
    const { index } = titleProps;
    const { activeIndex } = this.state;
    const newIndex = activeIndex === index ? -1 : index;

    this.setState({ activeIndex: newIndex });
  };

  handleSettings = () => {
    this.setState((prevState) => ({
      settings: !prevState.settings,
      changeUsername: this.state.username,
      changeFirst: this.state.firstName,
      changeLast: this.state.lastName,
      changeCity: this.state.city,
      changeProvince: this.state.province,
      changeCountry: this.state.country,
    }));
  };

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value });
  };

  handleCommit = () => {
    const profile = {
      registrations: {
        username: this.state.changeUsername,
        data: {
          city: this.state.changeCity,
          province: this.state.changeProvince,
          country: this.state.changeCountry,
        },
      },
      user: {
        firstName: this.state.changeFirst,
        lastName: this.state.changeLast,
      },
    };
    console.log(profile);
    console.log(JSON.stringify(profile));
    Axios.post(`http://10.171.204.164:8080/fusionauth/user`, profile)
      .then((response) => {
        console.log(response);
        this.setState((prevState) => ({
          settings: !prevState.settings,
          username: this.state.changeUsername,
          firstName: this.state.changeFirst,
          lastName: this.state.changeLast,
          city: this.state.changeCity,
          province: this.state.changeProvince,
          country: this.state.changeCountry,
        }));
      })
      .catch((error) => {
        console.log(error.response.data);
      });
  };

  handleMakeRequest = (role) => {
    const request = {
      role,
      userID: this.state.userID,
    };
    Axios.post(`http://10.171.204.164:8080/requests/make`, request).then(
      (response) => {
        console.log(response);
      }
    );
  };

  render() {
    const {
      activeIndex,
      settings,
      admin,
      expert,
      encoder,
      editor,
      roleText,
      changeUsername,
      changeFirst,
      changeLast,
      changeCity,
      changeProvince,
      changeCountry,
    } = this.state;

    return (
      <div>
        <Grid columns={2}>
          <Grid.Column width={4}>
            <Sticky>
              <h3>Role: {roleText}</h3>
              <Form>
                <Form.Input
                  fluid
                  label="Username"
                  name="changeUsername"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeUsername}
                  placeholder="Username"
                />
                <Form.Input
                  fluid
                  label="First Name"
                  name="changeFirst"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeFirst}
                  placeholder="First Name"
                />
                <Form.Input
                  fluid
                  label="Last Name"
                  name="changeLast"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeLast}
                  placeholder="Last Name"
                />
                <Form.Input
                  fluid
                  label="City"
                  name="changeCity"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeCity}
                  placeholder="City"
                />
                <Form.Input
                  fluid
                  label="Province"
                  name="changeProvince"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeProvince}
                  placeholder="State or Province"
                />
                <Form.Input
                  fluid
                  label="Country"
                  name="changeCountry"
                  readOnly={!settings}
                  onChange={this.handleChange}
                  value={changeCountry}
                  placeholder="Country"
                />
                {settings ? (
                  <Button.Group fluid>
                    <Button onClick={this.handleCommit}>Save</Button>
                    <Button onClick={this.handleSettings}>Cancel</Button>
                  </Button.Group>
                ) : (
                  <Button
                    fluid
                    toggle
                    active={settings}
                    onClick={this.handleSettings}
                  >
                    Change Info
                  </Button>
                )}
                {admin ? (
                  <Button fluid as={Link} to="/userlist">
                    User List
                  </Button>
                ) : (
                  ""
                )}
              </Form>
            </Sticky>
          </Grid.Column>

          <Grid.Column width={12}>
            {/* User projects */}
            <Accordion styled fluid>
              {this.state.documents.map((document, index) => {
                const link = `/document/${document.documentID}`;
                return (
                  <div key={document.documentID}>
                    <Accordion.Title
                      active={activeIndex === index}
                      index={index}
                      onClick={this.handleAccordion}
                    >
                      <Icon name="dropdown" />
                      {index + 1}. Document ID: {document.documentID},{" "}
                      {document.collection &&
                        `Collection: ${document.collection}`}{" "}
                      Status: {document.status}
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === index}>
                      <List>
                        {document.pdfDesc && (
                          <List.Item>Description: {document.pdfDesc}</List.Item>
                        )}
                        {document.docAbstract && (
                          <List.Item>
                            Abstract: {document.docAbstract}
                          </List.Item>
                        )}
                        {document.sortingDate && (
                          <List.Item>
                            Sorting Date: {document.sortingDate}
                          </List.Item>
                        )}
                        {document.dateAdded && (
                          <List.Item>
                            Date Added: {document.dateAdded.toLocaleString()}
                          </List.Item>
                        )}
                        <List.Item>
                          <Link to={link}>Link to document {index + 1}</Link>
                        </List.Item>
                      </List>
                    </Accordion.Content>
                  </div>
                );
              })}
            </Accordion>
            <Divider />
            {/*Notifications and Requests here */}
            {(expert || admin) && <Notifications />}
            {admin ? (
              <div>
                <Requests />
              </div>
            ) : (
              <div>
                {/*Requests roles here*/}
                <h1>Request a Role</h1>
                {!editor && (
                  <Modal
                    dimmer={"blurring"}
                    open={this.state.open === 0}
                    trigger={
                      <Button onClick={() => this.openModal(0)} fluid negative>
                        Editor
                      </Button>
                    }
                    onClose={this.close}
                  >
                    <Modal.Header>Request to become an Editor</Modal.Header>
                    <Modal.Content>
                      <p>Request to have the Editor role?</p>
                    </Modal.Content>
                    <Modal.Actions>
                      <Button onClick={this.close}>Cancel</Button>
                      <Button onClick={() => this.handleMakeRequest("Editor")}>
                        Confirm
                      </Button>
                    </Modal.Actions>
                  </Modal>
                )}
                {!encoder && (
                  <Modal
                    dimmer={"blurring"}
                    open={this.state.open === 1}
                    trigger={
                      <Button onClick={() => this.openModal(1)} fluid negative>
                        Encoder
                      </Button>
                    }
                    onClose={this.close}
                  >
                    <Modal.Header>Request to become an Encoder</Modal.Header>
                    <Modal.Content>
                      <p>Request to have the Encoder role?</p>
                    </Modal.Content>
                    <Modal.Actions>
                      <Button onClick={this.close}>Cancel</Button>
                      <Button onClick={() => this.handleMakeRequest("Encoder")}>
                        Confirm
                      </Button>
                    </Modal.Actions>
                  </Modal>
                )}
                {!expert && (
                  <Modal
                    dimmer={"blurring"}
                    open={this.state.open === 2}
                    trigger={
                      <Button onClick={() => this.openModal(2)} fluid negative>
                        Metadata Expert
                      </Button>
                    }
                    onClose={this.close}
                  >
                    <Modal.Header>
                      Request to become an Metadata Expert
                    </Modal.Header>
                    <Modal.Content>
                      <p>Request to have the Metadata Expert role?</p>
                    </Modal.Content>
                    <Modal.Actions>
                      <Button onClick={this.close}>Cancel</Button>
                      <Button
                        onClick={() =>
                          this.handleMakeRequest("Metadata Expert")
                        }
                      >
                        Confirm
                      </Button>
                    </Modal.Actions>
                  </Modal>
                )}
              </div>
            )}
          </Grid.Column>
        </Grid>
      </div>
    );
  }
}

export default UserPage;
