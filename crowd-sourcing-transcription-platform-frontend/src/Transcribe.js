import React from 'react';
import PDFViewer from './PDFViewer';
import QuillEditor from './QuillEditor';
import { Grid, Button } from 'semantic-ui-react';
import Axios from 'axios';

class Transcribe extends React.Component {
  state = {
    id: this.props.match.params.id,
    document: null,
    pdfURL: '',
    documentEdit: '',
    userID: '',
  };

  componentDidMount() {
    Axios.get(`http://10.171.204.164:8080/documents/${this.state.id}`).then(
      (response) => {
        console.log(response);
        this.setState({ document: response.data });
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
      this.setState({ userID: response.data.email }, () => {
        console.log(this.state);
      });
    });
  }

  handleSave = () => {
    console.log({
      docID: this.state.document.documentID,
      userID: this.state.userId,
      documentText: this.state.text,
    });
    Axios.post(`http://10.171.204.164:8080/transcriber/save`, {
      docID: this.state.document.documentID,
      userID: this.state.userId,
      documentText: this.state.text,
    }).then((response) => {
      console.log(response);
    });
  };

  handleTextChange = (content) => {
    this.setState({ documentEdit: content });
  };

  render() {
    if (this.state.userID != this.state.document?.user) {
    }
    return (
      <div>
        {this.state.userID !== this.state.document?.user ||
        this.state.document?.status !== 'Transcribing' ? (
          'No Permission'
        ) : (
          <Grid>
            <Grid.Column width={8}>
              <PDFViewer pdfURL={this.state.document?.pdfURL} />
            </Grid.Column>
            <Grid.Column width={8}>
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  height: '100%',
                }}
              >
                <QuillEditor
                  height='calc(100vh - 200px)'
                  handleChange={this.handleTextChange}
                  text={this.state.documentEdit}
                />
                <Button
                  style={{
                    borderRadius: '0px',
                    height: '100%',
                    flex: '1',
                    zIndex: '1000',
                  }}
                  fluid
                  onClick={this.handleSave}
                >
                  Save
                </Button>
              </div>
            </Grid.Column>
          </Grid>
        )}
      </div>
    );
  }
}

export default Transcribe;
