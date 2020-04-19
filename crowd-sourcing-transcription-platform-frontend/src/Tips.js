import React from 'react';
import { Embed, Segment, List, Grid, GridColumn } from 'semantic-ui-react';

const listStyle = {
  paddingBottom: '10px',
};

export default class Tips extends React.Component {
  render() {
    return (
      <div>
        <Grid>
          <GridColumn width='11'>
            <Segment>
              <Embed id='x_Sh4M5tLfs' source='youtube' />
            </Segment>
          </GridColumn>
          <GridColumn width='5'>
            <Segment stacked>
              <h1 style={{ textTransform: 'uppercase', fontStyle: 'italic' }}>
                Links
              </h1>
              <List>
                <List.Item style={listStyle}>
                  <List.Content>
                    <List.Header>
                      <a href='transcription-manual.pdf' download>
                        Transcription Manual
                      </a>
                    </List.Header>
                    <List.Description>
                      Explicit descriptions for transcribing
                    </List.Description>
                  </List.Content>
                </List.Item>
                <List.Item style={listStyle}>
                  <List.Header>
                    <a href='resources.pdf' download>
                      Resources for Transcription of Pemberton Family Papers
                    </a>
                  </List.Header>
                  <List.Description>
                    A list of resources to use for contextualizing and
                    understanding the letters
                  </List.Description>
                </List.Item>
                <List.Item style={listStyle}>
                  <List.Header as='a'>
                    <a href='paleography.pptx' download>
                      Paleography for Pemberton Papers
                    </a>
                  </List.Header>
                  <List.Description>
                    A powerpoint with tips on how to read and transcribe
                    manuscripts
                  </List.Description>
                </List.Item>
              </List>
            </Segment>
          </GridColumn>
        </Grid>
      </div>
    );
  }
}
