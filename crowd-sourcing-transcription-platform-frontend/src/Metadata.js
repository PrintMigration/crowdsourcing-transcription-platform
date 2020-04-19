import React from 'react';
import {
  Form,
  Segment,
  Grid,
  Button,
  Divider,
  Header,
  GridColumn,
  Icon,
  Transition,
  Input,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import Axios from 'axios';

class Metadata extends React.Component {
  state = {
    leftVisible: true,
    rightVisible: true,
    file: null,
  };

  handleLeftIconAnimation = () => {
    this.setState({ leftVisible: !this.state.leftVisible });
  };
  handleRightIconAnimation = () => {
    this.setState({ rightVisible: !this.state.rightVisible });
  };

  handleBatchUpload = (e) => {
    var form = new FormData();
    form.append('file', this.state.file);
    Axios.post('http://10.171.204.164:8080/documents/add-excel', form, {
      headers: {
        'content-type': 'multipart/form-data',
      },
    })
      .then((response) => {
        console.log(response);
      })
      .catch((error) => {
        console.log(error.response);
      });
  };

  render() {
    return (
      <Segment
        placeholder
        style={{ width: '70%', marginRight: 'auto', marginLeft: 'auto' }}
      >
        <Grid columns={2} stackable textAlign='center'>
          <Divider vertical>Or</Divider>
          <Grid.Row verticalAlign='middle'>
            <GridColumn>
              <div onMouseOver={this.handleLeftIconAnimation}>
                <Transition
                  animation='bounce'
                  visible={this.state.leftVisible}
                  duration={1500}
                >
                  <Header icon>
                    <Icon name='pencil alternate' />
                  </Header>
                </Transition>
                <Button
                  fluid
                  content='Manual'
                  size='large'
                  as={Link}
                  to='/upload/manual'
                />
              </div>
            </GridColumn>

            <GridColumn>
              <div onMouseOver={this.handleRightIconAnimation}>
                <Transition
                  animation='bounce'
                  visible={this.state.rightVisible}
                  duration={1500}
                >
                  <Header icon>
                    <Icon name='folder open outline' />
                  </Header>
                </Transition>
                <Input
                  type='file'
                  size='large'
                  fluid
                  style={{
                    marginBottom: '10px',
                    marginRight: 'auto',
                    marginLeft: 'auto',
                    maxWidth: '15rem',
                  }}
                  accept='.xlsx'
                  onChange={(e) => {
                    this.setState({ file: e.target.files[0] });
                  }}
                />
                <Button
                  fluid
                  content='Batch'
                  size='large'
                  onClick={this.handleBatchUpload}
                />
              </div>
            </GridColumn>
          </Grid.Row>
        </Grid>
      </Segment>
    );
  }
}

export default Metadata;
