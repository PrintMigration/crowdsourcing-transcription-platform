import React from 'react';
import {
  Tab,
  Form,
  Segment,
  Icon,
  Modal,
  Button,
  Grid,
  GridColumn,
  List,
  Transition,
  Header,
  Divider,
  Menu,
  Table,
  Input,
} from 'semantic-ui-react';
import Dropzone from 'react-dropzone';
import Axios from 'axios';
import Downshift from 'downshift';
import _ from 'lodash';
import PDFViewer from './PDFViewer';

const tabStyle = {
  display: 'inline-block',
  marginRight: '5px',
  marginBottom: '5px',
  borderRadius: '4px',
  padding: '5px 10px',
  width: 'fit-content',
  height: 'fit-content',
  backgroundColor: 'gainsboro',
};

class Navigation extends React.Component {
  render() {
    var left = false;
    var right = false;
    if (this.props.activeIndex == 0) {
      left = true;
    }
    if (this.props.activeIndex == 5) {
      right = true;
    }

    return (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <Button.Group size='mini' basic>
          <Button
            disabled={left}
            icon='left chevron'
            onClick={this.props.handlePrevious}
          />
          <Button
            disabled={right}
            icon='right chevron'
            onClick={this.props.handleNext}
          />
        </Button.Group>
      </div>
    );
  }
}

class TableRow extends React.Component {
  render() {
    return (
      <Table.Row>
        <Table.Cell
          width={6}
          style={{ textAlign: 'right', fontWeight: 'bold' }}
          content={this.props.label}
        />
        <Table.Cell content={this.props.value} />
      </Table.Row>
    );
  }
}

class EditMetadata extends React.Component {
  state = {
    id: this.props.match.params.id,

    saveModalVisible: false,
    deleteModalVisible: false,
    activeIndex: 0,
    files: [],

    doc: null,
    keywords: [],
    libraryOfCongresses: [],
    langDesc: '',
    repoDesc: '',
    repoLOD: '',
    repoURL: '',
    typeDesc: '',
    collection: '',
    customCitation: '',
    docAbstract: '',
    importID: '',
    internalPDFName: '',
    isJulian: false,
    letterDate: '',
    pdfDesc: '',
    pdfURL: '',
    sortingDate: '',
    keyword: '',
    keywordLOD: '',
    subjectHeading: '',
    roleDesc: '',

    orgs: [],
    orgRoles: [],
    dissolutionDate: '',
    formationDate: '',
    orgLOD: '',
    orgName: '',

    people: [],
    pRoles: [],
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

    places: [],
    placeRoles: [],
    country: '',
    county: '',
    latitude: '',
    longitude: '',
    placeDesc: '',
    placeLOD: '',
    stateProv: '',
    townCity: '',
    roleDesc: '',

    relatedDocs: [],
  };

  componentDidMount() {
    Axios.get(`http://10.171.204.164:8080/documents/${this.state.id}`).then(
      (response) => {
        console.log(response);
        let data = response.data;
        this.setState({
          keywords: data.keywords,
          libraryOfCongresses: data.libraryOfCongresses,
          langDesc: data.docLanguage?.langDesc,
          repoDesc: data.docRepository?.repoDesc,
          repoLOD: data.docRepository?.repoLOD,
          repoURL: data.docRepository?.repoURL,
          typeDesc: data.docType?.typeDesc,
          collection: data.collection,
          customCitation: data.customCitation,
          docAbstract: data.docAbstract,
          importID: data.importID,
          internalPDFName: data.internalPDFName,
          isJulian: data.isJulian,
          letterDate: data.letterDate,
          pdfDesc: data.pdfDesc,
          pdfURL: data.pdfURL,
          sortingDate: data.sortingDate,

          // orgs: data.orgs,
          // people: data.people,
          // places: data.places,
          // relatedDocs: data.RelatedDocuments,
        });
      }
    );
  }

  handleRangeChange = (e) => this.setState({ activeIndex: e.target.value });

  handleTabChange = (e, { activeIndex }) => this.setState({ activeIndex });

  handleNext = () => {
    this.setState({ activeIndex: this.state.activeIndex + 1 });
  };

  handlePrevious = () => {
    this.setState({ activeIndex: this.state.activeIndex - 1 });
  };

  onDrop = (acceptedFiles) => {
    let files = this.state.files.concat(acceptedFiles);
    this.setState({ files: files }, () => {
      console.log(this.state.files);
    });
  };

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value }, () => {
      console.log(this.state);
    });
  };

  handleState = (e) => {
    this.setState(e, () => {
      console.log(this.state);
    });
  };

  handleSubmitValidation = () => {
    console.log('enter');

    if (
      this.state.importID !== '' &&
      (this.state.repoDesc !== '' ||
        (this.state.repoLOD === '' && this.state.repoDesc === ''))
    ) {
      console.log('true');
      return true;
    } else {
      console.log('false');
      return false;
    }
  };

  handleDeleteDoc = () => {
    Axios.post(`http://10.171.204.164:8080/documents/delete-document`, null, {
      params: { docID: this.state.id },
    }).then((response) => {
      console.log(response);
    });
  };

  handleSubmit = () => {
    var relatedDocs = this.state.relatedDocs.map((doc) => {
      return doc.importID;
    });
    var body = {
      doc: {
        docLanguage: {
          langDesc: this.state.langDesc,
        },
        docRepository: {
          repoDesc: this.state.repoDesc,
          repoLOD: this.state.repoDesc,
          repoURL: this.state.repoURL,
        },
        docType: {
          typeDesc: this.state.typeDesc,
        },
        collection: this.state.collection,
        customCitation: this.state.customCitation,
        docAbstract: this.state.docAbstract,
        importID: this.state.importID,
        internalPDFName: this.state.internalPDFName,
        isJulian: this.state.isJulian,
        letterDate: this.state.letterDate,
        pdfDesc: this.state.pdfDesc,
        pdfURL: this.state.pdfURL,
        sortingDate: this.state.sortingDate,
        keywords: this.state.keywords,
        libraryOfCongresses: this.state.libraryOfCongresses,
      },
      orgs: this.state.orgs,
      people: this.state.people,
      places: this.state.places,
      relatedDocs: relatedDocs,
    };

    var form = new FormData();

    this.state.files.forEach((file) => {
      form.append('file', file);
    });

    form.append('importID', this.state.importID);
    console.log(this.state.files);
    console.log(form);
    Axios.post('http://10.171.204.164:8080/documents/upload', form, {
      headers: {
        'content-type': 'multipart/form-data',
      },
    }).then((response) => {
      body.doc.pdfURL = response.data;
      console.log(JSON.stringify(body));
      Axios.post('http://10.171.204.164:8080/documents/add-one-doc', body);
    });
  };

  render() {
    const { activeIndex } = this.state;
    const panes = [
      {
        menuItem: 'Upload Image',
        render: () => {
          return (
            <Tab.Pane
              style={{
                backgroundColor: '#F9FAFB',
                display: 'flex',
                flexDirection: 'column',
              }}
            >
              <UploadImage
                onDrop={this.onDrop}
                state={this.state}
                handleState={this.handleState}
              />
              <Navigation
                handlePrevious={this.handlePrevious}
                handleNext={this.handleNext}
                activeIndex={this.state.activeIndex}
              />
            </Tab.Pane>
          );
        },
      },
      {
        menuItem: 'Metadata',
        render: () => (
          <Tab.Pane style={{ backgroundColor: '#F9FAFB' }}>
            <MetadataForm
              handleState={this.handleState}
              handleChange={this.handleChange}
              state={this.state}
            />
            <Navigation
              handlePrevious={this.handlePrevious}
              handleNext={this.handleNext}
              activeIndex={this.state.activeIndex}
            />
          </Tab.Pane>
        ),
      },
      {
        menuItem: 'Documents',
        render: () => (
          <Tab.Pane style={{ backgroundColor: '#F9FAFB' }}>
            <RelatedDocuments
              handleState={this.handleState}
              handleChange={this.handleChange}
              state={this.state}
            />
            <Navigation
              handlePrevious={this.handlePrevious}
              handleNext={this.handleNext}
              activeIndex={this.state.activeIndex}
            />
          </Tab.Pane>
        ),
      },
      {
        menuItem: 'Organizations',
        render: () => (
          <Tab.Pane style={{ backgroundColor: '#F9FAFB' }}>
            <RelatedOrganizations
              handleState={this.handleState}
              handleChange={this.handleChange}
              state={this.state}
            />
            <Navigation
              handlePrevious={this.handlePrevious}
              handleNext={this.handleNext}
              activeIndex={this.state.activeIndex}
            />
          </Tab.Pane>
        ),
      },
      {
        menuItem: 'Places',
        render: () => (
          <Tab.Pane style={{ backgroundColor: '#F9FAFB' }}>
            <RelatedPlaces
              handleState={this.handleState}
              handleChange={this.handleChange}
              state={this.state}
            />
            <Navigation
              handlePrevious={this.handlePrevious}
              handleNext={this.handleNext}
              activeIndex={this.state.activeIndex}
            />
          </Tab.Pane>
        ),
      },
      {
        menuItem: 'People',
        render: () => (
          <Tab.Pane style={{ backgroundColor: '#F9FAFB' }}>
            <RelatedPeople
              handleState={this.handleState}
              handleChange={this.handleChange}
              state={this.state}
            />
            <Navigation
              handlePrevious={this.handlePrevious}
              handleNext={this.handleNext}
              activeIndex={this.state.activeIndex}
            />
          </Tab.Pane>
        ),
      },
      {
        menuItem: (
          <Menu.Item disabled key='submit'>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <Button
                secondary
                fluid
                content='Delete'
                onClick={() => {
                  this.setState({ deleteModalVisible: true });
                }}
                style={{ marginBottom: '10px' }}
              />
              <Button
                primary
                basic
                fluid
                content='Save'
                disabled={!this.handleSubmitValidation()}
                onClick={() => {
                  this.setState({ saveModalVisible: true });
                }}
              />
            </div>
          </Menu.Item>
        ),
      },
    ];

    return (
      <div>
        <Tab
          menu={{
            fluid: true,
            vertical: true,
            secondary: true,
            pointing: true,
          }}
          menuPosition='left'
          panes={panes}
          activeIndex={activeIndex}
          onTabChange={this.handleTabChange}
        />
        <Transition
          unmountOnHide
          visible={this.state.deleteModalVisible}
          animation='zoom'
        >
          <Modal
            onClose={() => {
              this.setState({ deleteModalVisible: false });
            }}
            open={true}
            size='mini'
          >
            <Modal.Header
              style={{
                display: 'flex',
                justifyContent: 'center',
              }}
            >
              <Header icon style={{ fontFamily: 'Abel', marginBottom: '0px' }}>
                <Icon style={{ color: '#be3b62' }} name='warning' />
                Delete
                <br />
                <div style={{ fontSize: '12px' }}>
                  Are you sure you want to delete this document?.
                </div>
              </Header>
            </Modal.Header>
            <Modal.Content style={{ textAlign: 'center' }}>
              All metadata associated with this document will be deleted.
            </Modal.Content>
            <Button
              basic
              fluid
              attached='bottom'
              style={{
                borderRadius: '0px',
                backgroundColor: '#be3b62',
                color: 'white',
              }}
              content='Go Back!'
              onClick={() => {
                this.setState({ deleteModalVisible: false });
              }}
            />
            <Button
              fluid
              attached='bottom'
              style={{
                backgroundColor: '#be3b62',
                color: 'white',
              }}
              content='Delete'
              onClick={this.handleDeleteDoc}
            />
          </Modal>
        </Transition>
        <Transition
          unmountOnHide
          visible={this.state.saveModalVisible}
          animation='zoom'
        >
          <Modal
            onClose={() => {
              this.setState({ saveModalVisible: false });
            }}
            open={true}
          >
            <Modal.Header
              style={{
                display: 'flex',
                justifyContent: 'center',
              }}
            >
              <Header icon style={{ fontFamily: 'Abel', marginBottom: '0px' }}>
                <Icon style={{ color: '#1a8041' }} name='check' />
                Submit
                <br />
                <p style={{ fontSize: '12px' }}>
                  Please check all values before submitting.
                </p>
              </Header>
            </Modal.Header>
            <Modal.Content scrolling>
              <Table compact='very'>
                <Table.Body>
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      PDF
                    </Table.Cell>
                  </Table.Row>
                  <TableRow
                    label='URL'
                    value={
                      this.state.files.length !== 0
                        ? 'Uploaded File'
                        : this.state.pdfURL
                    }
                  />
                  <TableRow label='Description' value={this.state.pdfDesc} />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Document
                    </Table.Cell>
                  </Table.Row>

                  <TableRow label='Import ID' value={this.state.importID} />
                  <TableRow
                    label='Internal Name'
                    value={this.state.internalPDFName}
                  />
                  <TableRow label='Language' value={this.state.langDesc} />
                  <TableRow label='Type' value={this.state.typeDesc} />
                  <TableRow label='Collection' value={this.state.collection} />
                  <TableRow label='Abstract' value={this.state.docAbstract} />
                  <TableRow
                    label='Custom Citation'
                    value={this.state.customCitation}
                  />
                  <TableRow
                    label='Library of Congresses'
                    value={this.state.libraryOfCongresses.map(
                      (libraryOfCongress) => {
                        return libraryOfCongress.subjectHeading + ', ';
                      }
                    )}
                  />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Repository
                    </Table.Cell>
                  </Table.Row>
                  <TableRow label='URL' value={this.state.repoURL} />
                  <TableRow label='LOD' value={this.state.repoLOD} />
                  <TableRow label='Description' value={this.state.repoDesc} />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Date
                    </Table.Cell>
                  </Table.Row>
                  <TableRow label='Sorting' value={this.state.sortingDate} />
                  <TableRow label='Letter' value={this.state.letterDate} />
                  <TableRow
                    label='Keywords'
                    value={this.state.keywords.map((keyword) => {
                      return keyword.keyword + ', ';
                    })}
                  />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Related Documents
                    </Table.Cell>
                  </Table.Row>
                  <TableRow
                    label='Related Documents'
                    value={this.state.relatedDocs.map((doc) => {
                      return doc.importID + ', ';
                    })}
                  />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Organizations
                    </Table.Cell>
                  </Table.Row>
                  <TableRow
                    label='Organizations'
                    value={this.state.orgs.map((org) => {
                      return org.org.orgName + ', ';
                    })}
                  />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      Places
                    </Table.Cell>
                  </Table.Row>
                  <TableRow
                    label='Places'
                    value={this.state.places.map((place) => {
                      return (
                        place.place.latitude +
                        ' ' +
                        place.place.longitude +
                        ', '
                      );
                    })}
                  />
                  <Table.Row>
                    <Table.Cell colSpan='2' style={{ textAlign: 'center' }}>
                      People
                    </Table.Cell>
                  </Table.Row>
                  <TableRow
                    label='People'
                    value={this.state.people.map((person) => {
                      return (
                        person.person.firstName +
                        ' ' +
                        person.person.lastName +
                        ', '
                      );
                    })}
                  />
                </Table.Body>
              </Table>
            </Modal.Content>
            <Button
              fluid
              attached='top'
              style={{
                borderRadius: '0px 0px 4px 4px',
                backgroundColor: '#1a8041',
                color: 'white',
              }}
              onClick={this.handleSubmit}
            >
              Submit!
            </Button>
          </Modal>
        </Transition>
      </div>
    );
  }
}

class UploadImage extends React.Component {
  render() {
    var display;
    var disabledFlag = this.props.state.pdfURL !== '';
    var disabled = 'You cannot upload a PDF and link a URL together!';
    if (this.props.state.files.length === 0) {
      display = (
        <div className='test' style={{ height: '100%' }}>
          <Segment
            placeholder={disabledFlag}
            style={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              height: '100%',
              width: '100%',
            }}
          >
            <h4 style={{ textTransform: 'uppercase', letterSpacing: '3px' }}>
              {disabledFlag ? disabled : 'Click or drag to upload'}
            </h4>
          </Segment>
        </div>
      );
    } else {
      display = (
        <div style={{ height: '100%' }}>
          <Segment style={{ height: '100%' }}>
            {this.props.state.files.map((file) => {
              return (
                <div>
                  <Icon name='file alternate outline' />
                  {file.name}
                </div>
              );
            })}
          </Segment>
          <Icon
            style={{
              zIndex: '1',
              position: 'absolute',
              top: '23px',
              right: '20px',
            }}
            name='x'
            link
            onClick={(e) => {
              this.props.handleState({ files: [] });
              e.stopPropagation();
            }}
          />
        </div>
      );
    }

    return (
      <div className='what' style={{ height: '100%', marginBottom: '15px' }}>
        <Dropzone
          multiple={false}
          accept='image/*, .pdf'
          disabled={this.props.state.pdfURL !== ''}
          onDrop={this.props.onDrop}
          multiple
        >
          {({ getRootProps, getInputProps }) => (
            <div {...getRootProps()} style={{ height: '100%', width: '100%' }}>
              <input {...getInputProps()} />
              {display}
            </div>
          )}
        </Dropzone>
      </div>
    );
  }
}

const headerStyle = {
  textTransform: 'uppercase',
  letterSpacing: '4px',
  fontWeight: 'bold',
  fontSize: '16px',
};

class MetadataForm extends React.Component {
  handleAddKeywords = () => {
    var keyword = {
      keyword: this.props.state.keyword,
      keywordLOD: this.props.state.keywordLOD,
    };
    var keywords = this.props.state.keywords.concat(keyword);
    this.props.handleState({ keywords: keywords, keyword: '' });
  };

  handleAddLibraryOfCongresses = () => {
    var libraryOfCongress = {
      subjectHeading: this.props.state.subjectHeading,
    };
    var libraryOfCongresses = this.props.state.libraryOfCongresses.concat(
      libraryOfCongress
    );
    this.props.handleState({
      libraryOfCongresses: libraryOfCongresses,
      subjectHeading: '',
    });
  };

  render() {
    return (
      <Form onSubmit={this.handleSubmit}>
        <Header style={headerStyle} dividing>
          PDF
        </Header>
        <Form.Input
          disabled={this.props.state.files.length !== 0}
          label='URL'
          name='pdfURL'
          value={this.props.state.pdfURL}
          onChange={this.props.handleChange}
          icon={
            <Modal
              trigger={
                <Icon
                  name='external'
                  link
                  disabled={this.props.state.pdfURL === ''}
                />
              }
            >
              <PDFViewer pdfURL={this.props.state.pdfURL} closeIcon />
            </Modal>
          }
        />
        <Form.TextArea
          label='Description'
          name='pdfDesc'
          value={this.props.state.pdfDesc}
          onChange={this.props.handleChange}
          rows={1}
        />
        <Header style={headerStyle} dividing>
          Document
        </Header>
        <Form.Group widths='equal'>
          <Form.Input
            label='Import ID'
            name='importID'
            value={this.props.state.importID}
            onChange={this.props.handleChange}
            error={
              this.props.state.importID === ''
                ? { content: 'Required Field', pointing: 'above' }
                : false
            }
          />
          <Form.Input
            label='Internal Name'
            name='internalPDFName'
            value={this.props.state.internalPDFName}
            onChange={this.props.handleChange}
          />
        </Form.Group>
        <Form.Group widths='equal'>
          <Form.Input
            label='Language'
            name='langDesc'
            value={this.props.state.langDesc}
            onChange={this.props.handleChange}
          />
          <Form.Input
            label='Type'
            name='typeDesc'
            value={this.props.state.typeDesc}
            onChange={this.props.handleChange}
          />
          <Form.Input
            label='Collection'
            name='collection'
            value={this.props.state.collection}
            onChange={this.props.handleChange}
          />
        </Form.Group>
        <Form.TextArea
          label='Abstract'
          name='docAbstract'
          value={this.props.state.docAbstract}
          onChange={this.props.handleChange}
        />
        <Form.TextArea
          label='Custom Citation'
          name='customCitation'
          value={this.props.state.customCitation}
          onChange={this.props.handleChange}
          rows={1}
        />
        <Form.Input
          label='Add Library of Congresses'
          name='subjectHeading'
          value={this.props.state.subjectHeading}
          onChange={this.props.handleChange}
          icon={
            <Icon
              name='add'
              circular
              link
              onClick={this.handleAddLibraryOfCongresses}
              disabled={this.props.state.subjectHeading === ''}
            />
          }
        />
        <div style={{ width: '100%' }}>
          {this.props.state.libraryOfCongresses.map(
            (libraryOfCongress, index) => {
              return (
                <div key={libraryOfCongress.subjectHeading} style={tabStyle}>
                  {libraryOfCongress.subjectHeading}
                  <Icon
                    style={{ marginLeft: '15px' }}
                    name='x'
                    link
                    onClick={() => {
                      var updatedLibraryOfCongresses = this.props.state
                        .libraryOfCongresses;
                      updatedLibraryOfCongresses.splice(index, 1);
                      this.setState({
                        libraryOfCongresses: updatedLibraryOfCongresses,
                      });
                    }}
                  />
                </div>
              );
            }
          )}
        </div>
        <Header style={headerStyle} dividing>
          Repository
        </Header>
        <Form.Group widths='equal'>
          <Form.Input
            label='URL'
            name='repoURL'
            value={this.props.state.repoURL}
            onChange={this.props.handleChange}
            error={
              (this.props.state.repoLOD !== '' ||
                this.props.state.repoDesc !== '') &&
              this.props.state.repoURL === ''
                ? { content: 'Required Field', pointing: 'above' }
                : false
            }
          />
          <Form.Input
            label='LOD'
            name='repoLOD'
            value={this.props.state.repoLOD}
            onChange={this.props.handleChange}
          />
        </Form.Group>
        <Form.TextArea
          label='Description'
          name='repoDesc'
          value={this.props.state.repoDesc}
          onChange={this.props.handleChange}
          rows={1}
        />
        <Header style={headerStyle} dividing>
          Date
        </Header>
        <Form.Group>
          <Form.Input
            width={8}
            label='Sorting'
            name='sortingDate'
            value={this.props.state.sortingDate}
            onChange={this.props.handleChange}
          />
          <Form.Input
            width={8}
            label='Letter'
            name='letterDate'
            value={this.props.state.letterDate}
            onChange={this.props.handleChange}
          />
          <Form.Radio
            style={{ margin: '30px 0px 2px 9.5px' }}
            toggle
            label='Julian'
            name='julian'
            onChange={() => {
              this.setState({ isJulian: !this.props.state.isJulian });
            }}
          />
        </Form.Group>
        <Divider />
        <Form.Input
          label='Add Keywords'
          name='keyword'
          value={this.props.state.keyword}
          onChange={this.props.handleChange}
          icon={
            <Icon
              name='add'
              circular
              link
              onClick={this.handleAddKeywords}
              disabled={this.props.state.keyword === ''}
            />
          }
        />
        <div style={{ width: '100%', marginBottom: '10px' }}>
          {this.props.state.keywords.map((keyword, index) => {
            return (
              <div key={keyword.keyword} style={tabStyle}>
                {keyword.keyword}
                <Icon
                  style={{ marginLeft: '15px' }}
                  name='x'
                  link
                  onClick={() => {
                    var updatedKeywords = this.props.state.keywords;
                    updatedKeywords.splice(index, 1);
                    this.props.handleState({ keywords: updatedKeywords });
                  }}
                />
              </div>
            );
          })}
        </div>
      </Form>
    );
  }
}

class RelatedDocuments extends React.Component {
  state = {
    source: [],
    results: [],
  };

  componentDidMount() {
    Axios.get('http://10.171.204.164:8080/documents/simplified-doc-info').then(
      (response) => {
        this.setState({ source: response.data });
      }
    );
  }

  render() {
    return (
      <Grid style={{ height: '650px', marginBottom: '0px' }}>
        <GridColumn width={10}>
          <Segment style={{ height: '100%' }}>
            <Downshift
              onChange={(selection) => {
                let relatedDocs = this.props.state.relatedDocs.concat(
                  selection
                );
                this.props.handleState({ relatedDocs: relatedDocs });
              }}
              itemToString={(item) => (item ? item.importID : '')}
            >
              {({
                getInputProps,
                getItemProps,
                getLabelProps,
                getMenuProps,
                isOpen,
                inputValue,
                highlightedIndex,
                selectedItem,
              }) => (
                <div>
                  <Input
                    {...getInputProps()}
                    size='large'
                    fluid
                    icon='search'
                  />
                  <div style={{ overflow: 'auto', height: '550px' }}>
                    {isOpen
                      ? this.state.source
                          .filter((item) => {
                            let re = new RegExp(
                              _.escapeRegExp(inputValue),
                              'i'
                            );
                            return !inputValue || re.test(item.importID);
                          })
                          .map((item, index) => {
                            return (
                              <div
                                {...getItemProps({
                                  key: item.importID,
                                  index,
                                  item,
                                  style: {
                                    width: '100%',
                                    padding: '10px',
                                    border: '1px solid #F9FAFB',
                                    borderTop: '0px',
                                    backgroundColor:
                                      highlightedIndex === index
                                        ? '#F9FAFB'
                                        : 'white',
                                    fontWeight:
                                      selectedItem === item ? 'bold' : 'normal',
                                  },
                                })}
                              >
                                <p
                                  style={{
                                    fontWeight: 'bold',
                                    fontSize: '20px',
                                    marginBottom: '0px',
                                  }}
                                >
                                  {item.importID}
                                </p>
                                <p
                                  style={{
                                    marginBottom: '0px',
                                    fontFamily: 'LTC-Bodoni-175',
                                    textTransform: 'uppercase',
                                    fontWeight: 'bold',
                                    fontSize: '10px',
                                    letterSpacing: '2px',
                                  }}
                                >
                                  {item.letterDate}
                                </p>
                                <p
                                  style={{
                                    marginBottom: '0px',
                                    width: '100%',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    display: '-webkit-box',
                                    WebkitBoxOrient: 'vertical',
                                    WebkitLineClamp: 3,
                                  }}
                                >
                                  {item.docAbstract}
                                </p>
                                <p
                                  style={{
                                    marginBottom: '0px',
                                    whiteSpace: 'nowrap',
                                    width: '100%',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    OTextOverflow: 'ellipsis',
                                  }}
                                >
                                  {item.keywords.map((keyword) => {
                                    return keyword.keyword + ', ';
                                  })}
                                </p>
                              </div>
                            );
                          })
                      : null}
                  </div>
                </div>
              )}
            </Downshift>
          </Segment>
        </GridColumn>
        <GridColumn width={6}>
          <Segment
            style={{ height: '100%', maxHeight: '622px', overflow: 'auto' }}
          >
            {this.props.state.relatedDocs.map((item, index) => {
              return (
                <Segment>
                  <p
                    style={{
                      fontWeight: 'bold',
                      fontSize: '20px',
                      marginBottom: '0px',
                    }}
                  >
                    {item.importID}
                  </p>
                  <p
                    style={{
                      marginBottom: '0px',
                      fontFamily: 'LTC-Bodoni-175',
                      textTransform: 'uppercase',
                      fontWeight: 'bold',
                      fontSize: '10px',
                      letterSpacing: '2px',
                    }}
                  >
                    {item.letterDate}
                  </p>
                  <p
                    style={{
                      marginBottom: '0px',
                      width: '100%',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitBoxOrient: 'vertical',
                      WebkitLineClamp: 3,
                    }}
                  >
                    {item.docAbstract}
                  </p>
                  <p
                    style={{
                      marginBottom: '0px',
                      width: '100%',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitBoxOrient: 'vertical',
                      WebkitLineClamp: 2,
                    }}
                  >
                    {item.keywords.map((keyword) => {
                      return keyword.keyword + ', ';
                    })}
                  </p>
                  <Icon
                    style={{
                      zIndex: '1',
                      position: 'absolute',
                      top: '7px',
                      right: '5px',
                    }}
                    name='x'
                    link
                    onClick={(e) => {
                      var relatedDocs = this.props.state.relatedDocs;
                      relatedDocs.splice(index, 1);
                      this.setState({ relatedDocs: relatedDocs });
                      e.stopPropagation();
                    }}
                  />
                </Segment>
              );
            })}
          </Segment>
        </GridColumn>
      </Grid>
    );
  }
}

class RelatedOrganizations extends React.Component {
  handleAddOrg = () => {
    var org = {
      org: {
        dissolutionDate: this.props.state.dissolutionDate,
        formationDate: this.props.state.formationDate,
        orgLOD: this.props.state.orgLOD,
        orgName: this.props.state.orgName,
      },
      orgRoles: this.props.state.orgRoles,
    };

    var orgs = this.props.state.orgs.concat(org);
    this.props.handleState({
      orgs: orgs,
      dissolutionDate: '',
      formationDate: '',
      orgLOD: '',
      orgName: '',
      orgRoles: [],
      roleDesc: '',
    });
  };

  handleAddOrgRoles = () => {
    var orgRole = {
      roleDesc: this.props.state.roleDesc,
    };
    var orgRoles = this.props.state.orgRoles.concat(orgRole);
    this.props.handleState({ orgRoles: orgRoles, roleDesc: '' });
  };

  handleDeleteOrg = (index) => {
    let orgs = this.props.state.orgs;
    orgs.splice(index, 1);
    this.setState({ orgs: orgs });
  };

  render() {
    return (
      <div>
        <Grid>
          <GridColumn width={10}>
            <Segment>
              <Form onSubmit={this.handleAddOrg}>
                <Form.Input
                  label='Name'
                  name='orgName'
                  value={this.props.state.orgName}
                  onChange={this.props.handleChange}
                  error={
                    this.props.state.orgName === ''
                      ? { content: 'Required Field', pointing: 'above' }
                      : false
                  }
                />
                <Form.Input
                  label='LOD'
                  name='orgLOD'
                  value={this.props.state.orgLOD}
                  onChange={this.props.handleChange}
                />
                <Form.Group widths='equal'>
                  <Form.Input
                    label='Formation Date'
                    name='formationDate'
                    value={this.props.state.formationDate}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    label='Dissolution Date'
                    name='dissolutionDate'
                    value={this.props.state.dissolutionDate}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>
                <Form.Input
                  label='Add Roles'
                  name='roleDesc'
                  value={this.props.state.roleDesc}
                  onChange={this.props.handleChange}
                  icon={
                    <Icon
                      name='add'
                      circular
                      link
                      onClick={this.handleAddOrgRoles}
                    />
                  }
                />
                <div style={{ width: '100%' }}>
                  {this.props.state.orgRoles.map((orgRole, index) => {
                    return (
                      <div key={orgRole.roleDesc} style={tabStyle}>
                        {orgRole.roleDesc}
                        <Icon
                          style={{ marginLeft: '15px' }}
                          name='x'
                          link
                          onClick={() => {
                            var updatedOrgRoles = this.props.state.orgRoles;
                            updatedOrgRoles.splice(index, 1);
                            this.props.handleState({ pRoles: updatedOrgRoles });
                          }}
                        />
                      </div>
                    );
                  })}
                </div>

                <Form.Button
                  floated='right'
                  circular
                  icon='add'
                  type='submit'
                  disabled={this.props.state.orgName === ''}
                />
              </Form>
            </Segment>
          </GridColumn>
          <GridColumn width={6}>
            <Segment
              attached='top'
              style={{
                height: '334.18px',
                overflow: 'auto',
                marginBottom: '0px',
              }}
            >
              <Transition.Group
                as={List}
                duration={200}
                divided
                verticalAlign='middle'
              >
                {this.props.state.orgs.map((org, index) => {
                  return (
                    <List.Item key={index}>
                      <Icon name='right triangle' />
                      <List.Content style={{ position: 'relative' }}>
                        <List.Header>{org.org.orgName}</List.Header>
                        <List.Description>
                          <b>LOD: </b> {org.org.orgLOD}
                          <br />
                          <b>Formation Date: </b> {org.org.formationDate}
                          <br />
                          <b>Dissolution Date: </b> {org.org.dissolutionDate}
                          <br />
                          <b>Roles: </b>
                          {org.orgRoles.map((role) => {
                            return role.roleDesc + ' ';
                          })}
                          <div
                            style={{
                              display: 'flex',
                              flexDirection: 'column',
                              position: 'absolute',
                              top: '0px',
                              right: '0px',
                            }}
                          >
                            <Icon
                              name='x'
                              link
                              onClick={() => {
                                this.handleDeleteOrg(index);
                              }}
                            />
                          </div>
                        </List.Description>
                      </List.Content>
                    </List.Item>
                  );
                })}
              </Transition.Group>
            </Segment>
            <Button
              fluid
              compact
              content='Clear'
              attached='bottom'
              onClick={() => {
                this.props.handleState({ orgs: [] });
              }}
            />
          </GridColumn>
        </Grid>
      </div>
    );
  }
}

class RelatedPeople extends React.Component {
  handleAddPerson = () => {
    var person = {
      person: {
        biography: this.props.state.biography,
        birthDate: this.props.state.birthDate,
        deathDate: this.props.state.deathDate,
        firstName: this.props.state.firstName,
        gender: this.props.state.gender,
        lastName: this.props.state.lastName,
        middleName: this.props.state.middleName,
        occupation: this.props.state.occupation,
        personLOD: this.props.state.personLOD,
        prefix: this.props.state.prefix,
        suffix: this.props.state.suffix,
        roleDesc: this.props.state.roleDesc,
      },
      pRoles: this.props.state.pRoles,
    };

    var people = this.props.state.people.concat(person);
    this.props.handleState({
      people: people,
      pRoles: [],
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
      roleDesc: '',
    });
  };

  handleAddPRoles = () => {
    var pRole = {
      roleDesc: this.props.state.roleDesc,
    };
    var pRoles = this.props.state.pRoles.concat(pRole);
    this.props.handleState({ pRoles: pRoles, roleDesc: '' });
  };

  handleDeletePerson = (index) => {
    let people = this.props.state.people;
    people.splice(index, 1);
    this.setState({ people: people });
  };

  handleChange = (e, { name, value }) => {
    this.props.handleState({ [name]: value }, () => {
      console.log(this.state);
    });
  };

  render() {
    return (
      <div>
        <Grid>
          <GridColumn width={10}>
            <Segment>
              <Form onSubmit={this.handleAddPerson}>
                <Form.Input
                  fluid
                  label='LOD'
                  name='personLOD'
                  value={this.props.state.personLOD}
                  onChange={this.props.handleChange}
                  error={
                    this.props.state.personLOD === ''
                      ? { content: 'Required Field', pointing: 'above' }
                      : false
                  }
                />
                <Form.Group widths='equal'>
                  <Form.Input
                    fluid
                    label='First Name'
                    name='firstName'
                    value={this.props.state.firstName}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    fluid
                    label='Middle Name'
                    name='middleName'
                    value={this.props.state.middleName}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    fluid
                    label='Last Name'
                    name='lastName'
                    value={this.props.state.lastName}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>
                <Form.Group>
                  <Form.Input
                    width={6}
                    label='Prefix'
                    name='prefix'
                    value={this.props.state.prefix}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    width={6}
                    label='Suffix'
                    name='suffix'
                    value={this.props.state.suffix}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    width={4}
                    label='Gender'
                    name='gender'
                    value={this.props.state.gender}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>

                <Form.Group widths='equal'>
                  <Form.Input
                    fluid
                    label='Birth Date'
                    name='birthDate'
                    value={this.props.state.birthDate}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    fluid
                    label='Death Date'
                    name='deathDate'
                    value={this.props.state.deathDate}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>
                <Form.Input
                  label='Occupation'
                  name='occupation'
                  value={this.props.state.occupation}
                  onChange={this.props.handleChange}
                />
                <Form.TextArea
                  label='Biography'
                  name='biography'
                  value={this.props.state.biography}
                  onChange={this.props.handleChange}
                />
                <Form.Input
                  label='Add Roles'
                  name='roleDesc'
                  value={this.props.state.roleDesc}
                  onChange={this.props.handleChange}
                  icon={
                    <Icon
                      name='add'
                      circular
                      link
                      onClick={this.handleAddPRoles}
                    />
                  }
                />
                <div style={{ width: '100%' }}>
                  {this.props.state.pRoles.map((pRole, index) => {
                    return (
                      <div key={pRole.roleDesc} style={tabStyle}>
                        {pRole.roleDesc}
                        <Icon
                          style={{ marginLeft: '15px' }}
                          name='x'
                          link
                          onClick={() => {
                            var updatedPRoles = this.props.state.pRoles;
                            updatedPRoles.splice(index, 1);
                            this.props.handleState({ pRoles: updatedPRoles });
                          }}
                        />
                      </div>
                    );
                  })}
                </div>

                <Form.Button
                  floated='right'
                  circular
                  icon='add'
                  type='submit'
                  disabled={this.props.state.personLOD === ''}
                />
              </Form>
            </Segment>
          </GridColumn>
          <GridColumn width={6}>
            <Segment
              attached='top'
              style={{
                height: '522.86px',
                overflow: 'auto',
              }}
            >
              <Transition.Group
                as={List}
                duration={200}
                divided
                verticalAlign='middle'
              >
                {this.props.state.people.map((person, index) => {
                  return (
                    <List.Item key={index}>
                      <Icon name='right triangle' />
                      <List.Content style={{ position: 'relative' }}>
                        <List.Header>
                          {person.person.firstName +
                            ' ' +
                            person.person.middleName +
                            ' ' +
                            person.person.lastName}
                        </List.Header>
                        <List.Description>
                          <b>Prefix: </b> {person.person.prefix}
                          <br />
                          <b>Suffix: </b> {person.person.suffix}
                          <br />
                          <b>Gender: </b> {person.person.gender}
                          <br />
                          <b>Birth Date: </b> {person.person.birthDate}
                          <br />
                          <b>Death Date: </b> {person.person.deathDate}
                          <br />
                          <b>Occupation: </b> {person.person.occupation}
                          <br />
                          <b>Biography: </b> {person.person.biography}
                          <br />
                          <b>Roles: </b>
                          {person.pRoles.map((role) => {
                            return role.roleDesc + ' ';
                          })}
                          <div
                            style={{
                              display: 'flex',
                              flexDirection: 'column',
                              position: 'absolute',
                              top: '0px',
                              right: '0px',
                            }}
                          >
                            <Icon
                              name='x'
                              link
                              onClick={() => {
                                this.handleDeletePerson(index);
                              }}
                            />
                          </div>
                        </List.Description>
                      </List.Content>
                    </List.Item>
                  );
                })}
              </Transition.Group>
            </Segment>
            <Button
              fluid
              compact
              content='Clear'
              attached='bottom'
              onClick={() => {
                this.props.handleState({ people: [] });
              }}
            />
          </GridColumn>
        </Grid>
      </div>
    );
  }
}

class RelatedPlaces extends React.Component {
  handleAddPerson = () => {
    var place = {
      place: {
        country: this.props.state.country,
        county: this.props.state.county,
        latitude: parseFloat(this.props.state.latitude),
        longitude: parseFloat(this.props.state.longitude),
        placeDesc: this.props.state.placeDesc,
        placeLOD: this.props.state.placeLOD,
        stateProv: this.props.state.stateProv,
        townCity: this.props.state.townCity,
      },
      placeRoles: this.props.state.placeRoles,
    };

    var places = this.props.state.places.concat(place);
    this.props.handleState({
      places: places,
      placeRoles: [],
      country: '',
      county: '',
      latitude: '',
      longitude: '',
      placeDesc: '',
      placeLOD: '',
      stateProv: '',
      townCity: '',
    });
  };

  handleAddPlaceRoles = () => {
    var placeRole = {
      roleDesc: this.props.state.roleDesc,
    };
    var placeRoles = this.props.state.placeRoles.concat(placeRole);
    this.props.handleState({ placeRoles: placeRoles, roleDesc: '' });
  };

  handleDeletePlace = (index) => {
    let places = this.props.state.places;
    places.splice(index, 1);
    this.setState({ places: places });
  };

  render() {
    return (
      <div>
        <Grid>
          <GridColumn width={10}>
            <Segment>
              <Form onSubmit={this.handleAddPerson}>
                <Form.Input
                  fluid
                  label='LOD'
                  name='placeLOD'
                  value={this.props.state.placeLOD}
                  onChange={this.props.handleChange}
                />
                <Form.Group widths='equal'>
                  <Form.Input
                    fluid
                    label='Country'
                    name='country'
                    value={this.props.state.country}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    fluid
                    label='State/Province'
                    name='stateProv'
                    value={this.props.state.stateProv}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>
                <Form.Group widths='equal'>
                  <Form.Input
                    fluid
                    label='Town/City'
                    name='townCity'
                    value={this.props.state.townCity}
                    onChange={this.props.handleChange}
                  />
                  <Form.Input
                    fluid
                    label='County'
                    name='county'
                    value={this.props.state.county}
                    onChange={this.props.handleChange}
                  />
                </Form.Group>
                <Form.TextArea
                  label='Description'
                  name='placeDesc'
                  value={this.props.state.placeDesc}
                  onChange={this.props.handleChange}
                />
                <Form.Group widths='equal'>
                  <Form.Input
                    fluid
                    label='Latitude'
                    name='latitude'
                    value={this.props.state.latitude}
                    onChange={this.props.handleChange}
                    error={
                      this.props.state.latitude === ''
                        ? { content: 'Required Field', pointing: 'above' }
                        : false
                    }
                  />
                  <Form.Input
                    fluid
                    label='Longitude'
                    name='longitude'
                    value={this.props.state.longitude}
                    onChange={this.props.handleChange}
                    error={
                      this.props.state.longitude === ''
                        ? { content: 'Required Field', pointing: 'above' }
                        : false
                    }
                  />
                </Form.Group>
                <Form.Input
                  label='Add Roles'
                  name='roleDesc'
                  value={this.props.state.roleDesc}
                  onChange={this.props.handleChange}
                  icon={
                    <Icon
                      name='add'
                      circular
                      link
                      onClick={this.handleAddPlaceRoles}
                    />
                  }
                />
                <div style={{ width: '100%' }}>
                  {this.props.state.placeRoles.map((placeRole, index) => {
                    return (
                      <div key={placeRole.roleDesc} style={tabStyle}>
                        {placeRole.roleDesc}
                        <Icon
                          style={{ marginLeft: '15px' }}
                          name='x'
                          link
                          onClick={() => {
                            var updatedPlaceRoles = this.props.state.placeRoles;
                            updatedPlaceRoles.splice(index, 1);
                            this.props.handleState({
                              placeRoles: updatedPlaceRoles,
                            });
                          }}
                        />
                      </div>
                    );
                  })}
                </div>

                <Form.Button
                  floated='right'
                  circular
                  icon='add'
                  type='submit'
                  disabled={
                    this.props.state.latitude === '' ||
                    this.props.state.longitude === ''
                  }
                />
              </Form>
            </Segment>
          </GridColumn>
          <GridColumn width={6}>
            <Segment
              attached='top'
              style={{
                height: '522.86px',
                overflow: 'auto',
              }}
            >
              <Transition.Group
                as={List}
                duration={200}
                divided
                verticalAlign='middle'
              >
                {this.props.state.places.map((place, index) => {
                  return (
                    <List.Item key={index}>
                      <Icon name='right triangle' />
                      <List.Content style={{ position: 'relative' }}>
                        <List.Header>
                          {place.place.latitude + ' ' + place.place.longitude}
                        </List.Header>
                        <List.Description>
                          <b>LOD: </b> {place.place.placeLOD}
                          <br />
                          <b>Country: </b> {place.place.country}
                          <br />
                          <b>State/Province: </b> {place.place.stateProv}
                          <br />
                          <b>Town/City: </b> {place.place.townCity}
                          <br />
                          <b>County: </b> {place.place.county}
                          <br />
                          <b>Description: </b> {place.place.placeDesc}
                          <br />
                          <b>Roles: </b>
                          {place.placeRoles.map((role) => {
                            return role.roleDesc + ' ';
                          })}
                          <div
                            style={{
                              display: 'flex',
                              flexDirection: 'column',
                              position: 'absolute',
                              top: '0px',
                              right: '0px',
                            }}
                          >
                            <Icon
                              name='x'
                              link
                              onClick={() => {
                                this.handleDeletePlace(index);
                              }}
                            />
                          </div>
                        </List.Description>
                      </List.Content>
                    </List.Item>
                  );
                })}
              </Transition.Group>
            </Segment>
            <Button
              fluid
              compact
              content='Clear'
              attached='bottom'
              onClick={() => {
                this.props.handleState({ places: [] });
              }}
            />
          </GridColumn>
        </Grid>
      </div>
    );
  }
}

export default EditMetadata;
