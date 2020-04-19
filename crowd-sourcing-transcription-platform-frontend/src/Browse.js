import React from 'react';
import axios from 'axios';
import {
  Menu,
  Grid,
  Divider,
  Sticky,
  Button,
  Icon,
  Segment,
} from 'semantic-ui-react';
import StackGrid from 'react-stack-grid';
import { Link } from 'react-router-dom';
import { Document, Page } from 'react-pdf/dist/entry.webpack';

const collections = [
  { key: 'collection1', name: 'Collection 1' },
  { key: 'collection2', name: 'Collection 2' },
  { key: 'collection3', name: 'Collection 3' },
  { key: 'collection4', name: 'Collection 4' },
  { key: 'collection5', name: 'Collection 5' },
  { key: 'collection6', name: 'Collection 6' },
  { key: 'collection7', name: 'Collection 7' },
  { key: 'collection8', name: 'Collection 8' },
];

const cardHeaderStyle = {
  fontFamily: 'Abel',
  textTransform: 'uppercase',
  fontSize: '20px',
  margin: '0px 0px 0px 0px',
  color: 'black',
};

const cardBodyStyle = { color: 'grey', fontFamily: 'Abel' };

class Browse extends React.Component {
  state = {
    pageNum: 0,
    documents: [],
    collections: [],
  };

  componentDidMount() {
    if (this.props.match.params.collection === undefined) {
      axios
        .get(
          `http://10.171.204.164:8080/documents/all?pageNum=${this.state.pageNum}&pageSize=12`
        )
        .then((response) => {
          this.setState({ documents: response.data.content }, () => {
            console.log(this.state.documents);
          });
        });
    } else {
      axios
        .post(
          `http://10.171.204.164:8080/documents/docs-from-collection?collection=${this.props.match.params.collection}&pageNum=${this.state.pageNum}&pageSize=12`
        )
        .then((response) => {
          console.log(response);
          this.setState({ documents: response.data }, () => {
            console.log(this.state.documents);
          });
        });
    }

    axios
      .get('http://10.171.204.164:8080/documents/collection')
      .then((response) => {
        this.setState({ collections: response.data }, () => {
          console.log(this.state.collections);
        });
      });
  }

  test = () => {
    this.grid.updateLayout();
  };

  render() {
    console.log(this.props.match.params.collection);
    return (
      <div>
        <Grid>
          <Grid.Column width={4}>
            <Sticky>
              <Menu
                style={{
                  minHeight: '50vh',
                  maxWidth: '100%',
                  fontFamily: 'LTC-Bodoni-175',
                }}
                text
                vertical
                pointing
                secondary
              >
                <Menu.Item
                  header
                  style={{
                    fontSize: '20px',
                    margin: '0px',
                  }}
                >
                  Collections
                </Menu.Item>
                <Divider />
                {this.state.collections.map((collection, index) => (
                  <Menu.Item
                    style={{
                      fontFamily: 'LTC-Bodoni-175',
                      fontWeight: 'bold',
                      fontStyle: 'italic',
                    }}
                    key={index}
                    as={Link}
                    to={`/browse/${collection}`}
                  >
                    {collection}
                  </Menu.Item>
                ))}
              </Menu>
            </Sticky>
          </Grid.Column>

          <Grid.Column width={12}>
            <StackGrid
              monitorImagesLoaded={true}
              columnWidth='33.33%'
              gutterWidth={20}
              gutterHeight={20}
              gridRef={(grid) => (this.grid = grid)}
            >
              {this.state.documents.map((document, index) => {
                const link = `/document/${document.documentID}`;
                var file;
                if (document.pdfURL === '') {
                  file = (
                    <div
                      style={{
                        backgroundColor: 'gainsboro',
                        height: '200px',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                      }}
                    >
                      <Icon
                        style={{ color: 'black' }}
                        name='file pdf outline'
                        size='large'
                      />
                    </div>
                  );
                } else {
                  file = (
                    <Document
                      file={`https://cors-anywhere.herokuapp.com/${document.pdfURL}`}
                    >
                      <Page
                        renderAnnotationLayer={false}
                        pageNumber={2}
                        width={236.33}
                      />
                    </Document>
                  );
                }
                return (
                  <div key={index} style={{ height: '100%' }}>
                    <Link to={link}>
                      {file}
                      <h1 style={cardHeaderStyle}>{document.docAbstract}</h1>
                      <p style={cardBodyStyle}>{document.collection}</p>
                    </Link>
                  </div>
                );
              })}
            </StackGrid>
          </Grid.Column>
        </Grid>
      </div>
    );
  }
}

export default Browse;
