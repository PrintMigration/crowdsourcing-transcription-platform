import React from 'react';
import { Grid, Button } from 'semantic-ui-react';
import PDFViewer from './PDFViewer';
import Axios from 'axios';
import QuillEditor from './QuillEditor';

export default class Edit extends React.Component {
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

  handleSave = () => {};

  handleTextChange = (content) => {
    this.setState({ documentEdit: content });
  };

  render() {
    return (
      <div>
        {this.state.userID !== this.state.document?.user ||
        this.state.document?.status !== 'Editing' ? (
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
