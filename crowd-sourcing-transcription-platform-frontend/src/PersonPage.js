import React from 'react';
import { Segment, Grid, Divider, Transition, Icon } from 'semantic-ui-react';
import EditableText from './EditableText';
import ContentEditable from 'react-contenteditable';
import Axios from 'axios';

export default class PersonPage extends React.Component {
  state = {
    id: this.props.match.params.id,
    editing: false,
    personID: null,
    biography: '',
    birthDate: '',
    deathDate: '',
    firstName: '',
    gender: '',
    lastName: '',
    middleName: '',
    occupation: '',
    personLOD: '',
    prefix: '',
    suffix: '',
  };
  componentDidMount() {
    Axios.get(`http://10.171.204.164:8080/person/${this.state.id}`).then(
      (response) => {
        console.log(response.data);
        let person = response.data;
        if (person !== null) {
          this.setState(
            {
              personID: person?.personID,
              biography: person?.biography,
              birthDate: person?.birthDate,
              deathDate: person?.deathDate,
              firstName: person?.firstName,
              gender: person?.gender,
              lastName: person?.lastName,
              middleName: person?.middleName,
              occupation: person?.occupation,
              personLOD: person?.personLOD,
              prefix: person?.prefix,
              suffix: person?.suffix,
            },
            () => {
              this.setState({ initialState: this.state });
            }
          );
        }
      }
    );
  }

  handleEditText = (e, name) => {
    this.setState({ [name]: e.target.value }, () => {
      console.log(this.state);
    });
  };

  render() {
    return (
      <Segment secondary style={{ marginBottom: '100px' }}>
        <div
          style={{
            position: 'absolute',
            top: '32px',
            right: '32px',
            zIndex: '1001',
          }}
        >
          <Transition
            directional
            animation='swing down'
            duration={{ hide: 0, show: 1000 }}
            visible={!this.state.editing}
          >
            <Icon
              circular
              name='edit'
              link
              style={{
                color: '#1a8041',
                boxShadow: '0em 0em 0em 0.1em rgba(26, 128, 65, 0.25) inset',
              }}
              onClick={(e) => {
                this.setState({ editing: true });
                e.stopPropagation();
              }}
            />
          </Transition>
        </div>
        <div
          style={{
            position: 'absolute',
            top: '32px',
            right: '32px',
            zIndex: '1000',
          }}
        >
          <Transition
            visible={this.state.editing}
            animation='swing down'
            duration={{ hide: 100, show: 500 }}
            children={
              <Icon
                circular
                name='x'
                link
                onClick={(e) => {
                  this.setState(this.state.initialState);
                  e.stopPropagation();
                }}
              />
            }
          />
        </div>
        <div
          style={{
            position: 'absolute',
            top: '68.5px',
            right: '32px',
            zIndex: '1000',
          }}
        >
          <Transition
            visible={this.state.editing}
            animation='swing down'
            duration={{ hide: 150, show: 700 }}
            children={
              <Icon
                circular
                name='trash alternate outline'
                link
                onClick={(e) => {
                  e.stopPropagation();
                }}
                style={{
                  color: '#be3b62',
                  boxShadow: '0em 0em 0em 0.1em rgb(190, 59, 98, 0.25) inset',
                }}
              />
            }
          />
        </div>
        <div
          style={{
            position: 'absolute',
            top: '105px',
            right: '32px',
            zIndex: '1000',
          }}
        >
          <Transition
            visible={this.state.editing}
            animation='swing down'
            duration={{ hide: 200, show: 600 }}
            children={
              <Icon
                circular
                name='save outline'
                link
                onClick={(e) => {
                  this.handleSave();
                  e.stopPropagation();
                }}
                style={{
                  color: '#1a8041',
                  boxShadow: '0em 0em 0em 0.1em rgba(26, 128, 65, 0.25) inset',
                }}
              />
            }
          />
        </div>
        <Grid>
          <Grid.Row>
            <Grid.Column>
              <Segment
                style={{
                  color: 'black',
                  fontSize: '50px',
                  fontWeight: 'bold',
                  textTransform: 'uppercase',
                  letterSpacing: '5px',
                  lineHeight: '50px',
                  display: 'flex',
                  justifyContent: 'center',
                }}
              >
                <ContentEditable
                  html={this.state.firstName}
                  style={{ marginRight: '20px' }}
                  disabled={!this.state.editing}
                />
                <ContentEditable
                  html={this.state.lastName}
                  style={{ marginRight: '20px' }}
                  disabled={!this.state.editing}
                />
                <ContentEditable
                  html={this.state.lastName}
                  disabled={!this.state.editing}
                />
              </Segment>
            </Grid.Column>
          </Grid.Row>
          <Grid.Row>
            <Grid.Column width={10}>
              <Segment style={{ fontSize: '20px' }}>
                <EditableText
                  label='LOD'
                  name='personLOD'
                  placeholder={this.state.personLOD}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <Divider />
                <EditableText
                  label='Prefix'
                  name='prefix'
                  placeholder={this.state.prefix}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <EditableText
                  label='Suffix'
                  name='suffix'
                  placeholder={this.state.suffix}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <Divider />
                <EditableText
                  label='Birth Date'
                  name='birthDate'
                  placeholder={this.state.birthDate}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <EditableText
                  label='Death Date'
                  name='deathDate'
                  placeholder={this.state.deathDate}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <Divider />
                <EditableText
                  label='Gender'
                  name='gender'
                  placeholder={this.state.gender}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
                <Divider />
                <EditableText
                  label='Occupation'
                  name='occupation'
                  placeholder={this.state.occupation}
                  editing={this.state.editing}
                  onChange={this.handleEditText}
                />
              </Segment>
            </Grid.Column>
            <Grid.Column width={6}>
              <Segment style={{ height: '100%', overflow: 'auto' }}>
                <h3
                  style={{
                    textAlign: 'center',
                    textTransform: 'uppercase',
                    letterSpacing: '5px',
                  }}
                >
                  Biography
                </h3>
                <Divider />
                <ContentEditable
                  html={this.state.biography}
                  disabled={!this.state.editing}
                  style={{
                    width: '100%',
                    borderWidth: '0px 0px 1px 0px',
                    borderColor: this.state.editing ? 'lightgray' : 'white',
                    borderStyle: 'dashed',
                    padding: '2px 4px',
                    lineBreak: 'anywhere',
                  }}
                />
              </Segment>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Segment>
    );
  }
}
