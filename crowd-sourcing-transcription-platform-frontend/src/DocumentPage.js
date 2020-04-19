import React from 'react';
import Axios from 'axios';
import {
  Grid,
  Tab,
  Button,
  Modal,
  Divider,
  Form,
  Icon,
  Transition,
} from 'semantic-ui-react';
import PDFViewer from './PDFViewer';
import ReactQuill from 'react-quill';
import { Link } from 'react-router-dom';
import EditableText from './EditableText';

const dropdownStatus = [
  {
    key: 'Select Status',
    text: 'Select Status',
    value: '',
  },
  {
    key: 'Needs Transcribing',
    text: 'Needs Transcribing',
    value: 'Needs Transcribing',
  },
  {
    key: 'Needs Editing',
    text: 'Needs Editing',
    value: 'Needs Editing',
  },
  {
    key: 'Needs TEI Encoding',
    text: 'Needs TEI Encoding',
    value: 'Needs TEI Encoding',
  },
  {
    key: 'Completed',
    text: 'Completed',
    value: 'Completed',
  },
];

class DocumentPage extends React.Component {
  state = {
    editing: false,
    open: null,
    status: '',
    id: this.props.match.params.id,
    document: null,
    documentEdit: null,
    userID: '',
    admin: false,
    importID: '',
    typeDesc: '',
    langDesc: '',
    pdfDesc: '',
    collection: '',
    sortingDate: '',
    letterDate: '',
    docAbstract: '',
    initialState: null,
  };

  openModal = (id) => this.setState({ open: id });

  close = () => this.setState({ open: null });

  componentDidMount() {
    Axios.get(`http://10.171.204.164:8080/documents/${this.state.id}`).then(
      (response) => {
        console.log(response.data);
        this.setState({
          document: response.data,
          importID: response.data.importID,
          typeDesc: response.data.docType?.typeDesc,
          langDesc: response.data.docLanguage?.langDesc,
          pdfDesc: response.data.pdfDesc,
          collection: response.data.collection,
          sortingDate: response.data.sortingDate,
          letterDate: response.data.letterDate,
          docAbstract: response.data.docAbstract,
          initialState: {
            editing: false,
            document: response.data,
            importID: response.data.importID,
            typeDesc: response.data.docType?.typeDesc,
            langDesc: response.data.docLanguage?.langDesc,
            pdfDesc: response.data.pdfDesc,
            collection: response.data.collection,
            sortingDate: response.data.sortingDate,
            letterDate: response.data.letterDate,
            docAbstract: response.data.docAbstract,
          },
        });
      }
    );
    Axios.get(
      `http://10.171.204.164:8080/edits/document/${this.state.id}`
    ).then((response) => {
      if (response.data == '') {
        this.setState({ documentEdit: '' });
      } else {
        this.setState({ documentEdit: response.data.documentText });
      }
    });
    Axios.get(`http://10.171.204.164:8080/fusionauth/user`, {
      withCredentials: true,
    }).then((response) => {
      console.log(response);
      this.setState({
        userID: response.data.email,
        userRoles: response.data.registrations[0].roles,
      });
      if (response.data.registrations[0].roles.indexOf('Administrator') > -1) {
        this.setState({ admin: true });
      }
    });
  }

  handleEditText = (e, name) => {
    this.setState({ [name]: e.target.value }, () => {
      console.log(this.state);
    });
  };

  handleCheckOut = () => {
    if (this.state.documentUser != this.state.userID) {
      Axios.post('http://10.171.204.164:8080/transcriber/checkout', {
        docID: this.state.id,
        userID: this.state.userID,
      });
    }
  };

  handleDeleteDoc = () => {
    const document = this.state.document;
    Axios.post('http://10.171.204.164:8080/delete-document', {
      document,
    });
  };

  handleStatusChange = (e, data) => {
    this.setState({ status: data.value });
  };

  handleStatusCommit = () => {
    if (this.state.status) {
      Axios.post(
        `http://10.171.204.164:8080/change-status?docID=${this.state.id}&status=${this.state.status}`
      )
        .then((response) => {
          console.log(response);
          this.setState({ open: null });
        })
        .catch((error) => {
          console.log(error.response.data);
        });
    }
  };

  render() {
    let status = this.state.document?.status;
    let documentUser = this.state.document?.user;
    let user = this.state.userID;
    let text,
      to,
      onClick,
      disabled = false;

    if (user === '') {
      text = 'You are not signed in.';
      disabled = true;
    } else if (user === documentUser) {
      if (status === 'Transcribing') {
        text = 'Transcribe';
        to = `/transcribe/${this.state.id}`;
      } else if (status === 'Editing') {
        text = 'Edit';
        to = `/edit/${this.state.id}`;
      } else if (status === 'TEI Encoding') {
        text = 'Encode';
        // to = `/encode/${this.state.id}`
      }
    } else if (documentUser === null) {
      if (status === 'Needs Transcribing') {
        text = 'Transcribe';
        to = `/transcribe/${this.state.id}`;
        onClick = () => {
          let body = {
            docID: this.state.id,
            userID: user,
          };
          Axios.post(`http://10.171.204.164:8080/transcriber/checkout`, body);
        };
      } else if (
        status === 'Needs Editing' &&
        this.state.userRoles?.includes('Editor')
      ) {
        text = 'Edit';
        to = `/edit/${this.state.id}`;
        onClick = () => {
          let body = {
            docID: this.state.id,
            userID: user,
          };
          Axios.post(`http://10.171.204.164:8080/editor/checkout`, body);
        };
      } else if (
        status === 'Needs TEI Encoding' &&
        this.state.userRoles?.includes('Encoder')
      ) {
        text = 'Encode';
        // to = `/edit/${this.state.id}`;
        onClick = () => {
          let body = {
            docID: this.state.id,
            userID: user,
          };
          Axios.post(`http://10.171.204.164:8080/encoder/checkout`, body);
        };
      }
    } else if (status === 'Completed') {
      text = 'Completed!';
      disabled = true;
    } else if (user !== documentUser) {
      text = 'Checked Out';
      disabled = true;
    }

    var button = (
      <Button as={Link} to={to} onClick={onClick} disabled={disabled}>
        {text}
      </Button>
    );

    var metadataEditButton;
    if (
      this.state.userRoles?.includes('Metadata Expert') ||
      this.state.userRoles?.includes('Admin')
    ) {
      metadataEditButton = (
        <Button
          content='Edit Metadata'
          as={Link}
          to={`/metadata/${this.state.id}`}
        />
      );
    }

    var editButtonVisible;
    if (
      !this.state.userRoles?.includes('Metadata Expert') &&
      !this.state.userRoles?.includes('Admin')
    ) {
      editButtonVisible = { display: 'none' };
    }

    var panes = [
      {
        menuItem: 'Transcription',
        render: () => (
          <ReactQuill
            style={{
              borderStyle: 'solid',
              borderColor: 'lightgray',
              borderWidth: '1px',
              height: 'calc(100vh - 252px)',
            }}
            readOnly={true}
            theme='bubble'
            placeholder='This document has not been transcribed yet.'
            value={this.state.documentEdit}
          />
        ),
      },
      {
        menuItem: 'Metadata',
        render: () => {
          return (
            <Tab.Pane
              style={{
                height: 'calc(100vh - 252px)',
                borderRadius: '0px',
                margin: '0px',
                width: '100%',
                fontSize: '16px',
                padding: '0px',
              }}
            >
              <div style={editButtonVisible}>
                <div
                  style={{
                    position: 'absolute',
                    bottom: '16px',
                    right: '16px',
                  }}
                ></div>
              </div>
            </Tab.Pane>
          );
        },
      },
    ];

    return (
      <div>
        <Grid>
          <Grid.Column width={8}>
            <PDFViewer pdfURL={this.state.document?.pdfURL} />
          </Grid.Column>
          <Grid.Column
            width={8}
            style={{
              position: 'relative',
              display: 'flex',
              flexDirection: 'column',
            }}
          >
            <Tab
              menu={{ secondary: true, pointing: true }}
              panes={panes}
              attached='top'
            />
            <Button.Group attached='bottom' style={{ height: '100%' }}>
              {metadataEditButton}
              {button}
            </Button.Group>
          </Grid.Column>
        </Grid>
        {this.state.admin && (
          <div>
            <Divider />
            <Form>
              <Form.Group inline>
                <Form.Select
                  options={dropdownStatus}
                  value={this.state.status}
                  placeholder='Select Status'
                  onChange={this.handleStatusChange}
                />
                <Modal
                  dimmer={'blurring'}
                  open={this.state.open === 0}
                  trigger={
                    <Form.Button
                      onClick={() => this.openModal(0)}
                      fluid
                      negative
                    >
                      Change Status
                    </Form.Button>
                  }
                  onClose={this.close}
                >
                  <Modal.Header>Change Status</Modal.Header>
                  <Modal.Content>
                    <p>
                      Warning: Are you sure you want to change this documents
                      status?
                    </p>
                  </Modal.Content>
                  <Modal.Actions>
                    <Button onClick={this.close}>Cancel</Button>
                    <Button onClick={() => this.handleStatusCommit()}>
                      Change Status
                    </Button>
                  </Modal.Actions>
                </Modal>
              </Form.Group>
            </Form>
          </div>
        )}
      </div>
    );
  }
}

export default DocumentPage;
