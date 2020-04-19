import React from "react";
import axios from "axios";
import {
  Button,
  Form,
  Segment,
  Header,
  Grid,
  GridColumn,
  Message,
  Accordion,
  Icon,
  Table,
  Pagination,
  Transition,
} from "semantic-ui-react";

// import form fields that work dynamically
import AddPersonSearch from "./AddPersonSearch";
import AddPlaceSearch from "./AddPlaceSearch";
import AddKeywordSearch from "./AddKeywordSearch";
import AddOrganizationSearch from "./AddOrganizationSearch";

// The set of key value pairs that are used for the dropdown selector called 'status'
const dropdownStatus = [
  {
    key: "Select Status",
    text: "Select Status",
    value: "",
  },
  {
    key: "Needs Transcribing",
    text: "Needs Transcribing",
    value: "Needs Transcribing",
  },
  {
    key: "Transcribing",
    text: "Transcribing",
    value: "Transcribing",
  },
  {
    key: "Needs Editing",
    text: "Needs Editing",
    value: "Needs Editing",
  },
  {
    key: "Editing",
    text: "Editing",
    value: "Editing",
  },
  {
    key: "Needs TEI Encoding",
    text: "Needs TEI Encoding",
    value: "Needs TEI Encoding",
  },
  {
    key: "TEI Encoding",
    text: "TEI Encoding",
    value: "TEI Encoding",
  },
  {
    key: "Completed",
    text: "Completed",
    value: "Completed",
  },
];

const titleStyle = {
  textTransform: "uppercase",
  letterSpacing: "3px",
  fontWeight: "bold",
  fontSize: "10px",
};

const headerStyle = {
  textTransform: "uppercase",
  letterSpacing: "4px",
  fontWeight: "bold",
  fontSize: "16px",
};

class RobustSearch extends React.Component {
  // We change the state using the onChange function as the user types into the multiple fields on the form.
  state = {
    activePage: 1,
    search: null,
    totalPages: 0,
    results: [],
    activeIndex: 0,
    pushed: false,
    collection: "",
    // prdSet is an array of people and their roles tied to the document.
    // person and role are split into 2 different objects to match what backend team wanted
    prdSet: [
      {
        person: {
          biography: "",
          firstName: "",
          lastName: "",
          middleName: "",
          occupation: "",
          personLOD: "",
          prefix: "",
          suffix: "",
        },
        role: { roleDesc: "" },
      },
    ],
    // array of places that are tied to the document we are searching for
    placerdSet: [
      {
        place: {
          country: "",
          county: "",
          placeDesc: "",
          placeLOD: "",
          stateProv: "",
          townCity: "",
        },
        role: { roleDesc: "" },
      },
    ],
    religion: {
      religionDesc: "",
    },
    dropdownReligion: [],
    ordSet: [{ org: { orgLOD: "", orgName: "" }, role: { roleDesc: "" } }], // array of organizations we are searching for
    keywords: [{ keyword: "" }], // array of keywords we are searching for
    // Following 2 variables are later combined to form sortingDate inside the onSubmit fuction
    dateFrom: "",
    dateTo: "",
    status: "", // status is selected by a dropdown menu. We set Select Status as what it shows when you haven't selected anything for that field.
    typeDesc: "",
    langDesc: "",
    sortBy: "importID",
    direction: "asce",
    errors: {}, // we can place any validation errors in this array.
  };

  componentDidMount = () => {
    // place documents tied to the user in this.state.documents
    const religionList = [];
    axios.get(`http://10.171.204.164:8080/religion`).then((response) => {
      console.log(response.data);
      if (response.data.length > 0) {
        response.data.map((religion) => {
          religionList.push({
            key: religion.religionDesc,
            text: religion.religionDesc,
            value: religion.religionDesc,
          });
        });
        this.setState(
          {
            dropdownReligion: religionList,
          },
          () => {
            console.log(this.state.results);
          }
        );
      }
    });
  };

  // call this function when the user click the submit button
  onSubmit = (e) => {
    // After validating user input, we place the needed data inside search
    if (this.validation()) {
      var date;
      if (this.state.dateFrom !== "" && this.state.dateTo !== "") {
        date = this.state.dateFrom + "-" + this.state.dateTo;
      } else {
        date = this.state.dateFrom + "" + this.state.dateTo;
      }
      const search = {
        doc: {
          collection: this.state.collection,
          customCitation: "",
          docAbstract: "",
          pdfDesc: "",
          importID: "",
          internalPDFName: "",
          isJulian: null,
          sortingDate: date, // we combine dateFrom and dateTo here into one string.
          status: this.state.status,
          docLanguage: {
            langDesc: this.state.langDesc,
          },
          docRepository: {
            repoDesc: "",
            repoLOD: "",
          },
          docType: {
            typeDesc: this.state.typeDesc,
          },
          edits: [{ plainText: "" }],
          keywords: this.state.keywords,
          libraryOfCongresses: [{ subjectHeading: "" }],
          ordSet: this.state.ordSet,
          placerdSet: this.state.placerdSet,
          prdSet: this.state.prdSet,
          religion: {
            religionDesc: "",
          },
        },
        religion: this.state.religion,
        sortBy: this.state.sortBy, //Default value
        direction: this.state.direction, //Default value
      };
      // for now, we print to the console, but we can send the data to the backend here.
      console.log(JSON.stringify(search));
      axios
        .post(
          "http://10.171.204.164:8080/documents/user-search?pageNum=0&pageSize=15",
          search
        )
        .then((response) => {
          console.log(response);
          this.setState(
            {
              results: response.data.content,
              totalPages: response.data.totalPages + 1,
              pushed: true,
              search: search,
              activePage: 1,
            },
            () => {
              console.log(this.state);
            }
          );
        });
    }
  };

  // This function checks to see if there are any sort of user errors before we send to the backend.
  // (or print to the console)
  validation = () => {
    // create the variables we want to verify or change here.
    var dateFrom = this.state.dateFrom;
    var dateTo = this.state.dateTo;
    var errors = {};

    var validForm = true;

    // both dateFrom and dateTo must be empty or filled.
    // Backend asked that we don't just send one or the other.
    // We also make sure they typed in a number.
    if (
      (dateFrom !== "" && dateTo === "") ||
      (dateFrom === "" && dateTo !== "")
    ) {
      validForm = false;
      errors["dates"] = "Please input both a starting and ending year";
    } else if (isNaN(dateFrom) || isNaN(dateTo)) {
      validForm = false;
      errors["dates"] = "Please input starting and end year as numbers";
    }

    // if any errors occured, change the state to match.
    this.setState({
      errors,
    });

    // returns true if none of the if statements were triggered.
    return validForm;
  };

  handleChange = (e, { name, value }) => this.setState({ [name]: value });

  // onChange function used only by status dropdown.
  onChangeStatus = (e, data) => {
    this.setState({ status: data.value });
  };

  onChangeReligion = (e, data) => {
    this.setState({ religion: { religionDesc: data.value } });
  };

  handlePersonSearch = (prdSet) => {
    this.setState({ prdSet: prdSet });
  };

  handlePlacesSearch = (placerdSet) => {
    this.setState({ placerdSet: placerdSet });
  };

  handleOrganizationSearch = (ordSet) => {
    this.setState({ ordSet: ordSet });
  };

  handleKeywordSearch = (keywords) => {
    this.setState({ keywords: keywords });
  };

  handleAccordion = (e, titleProps) => {
    const { index } = titleProps;
    const { activeIndex } = this.state;
    const newIndex = activeIndex === index ? -1 : index;

    this.setState({ activeIndex: newIndex });
  };

  handlePageChange = (event, data) => {
    console.log(data);
    axios
      .post(
        `http://10.171.204.164:8080/documents/user-search?pageNum=${
          data.activePage - 1
        }&pageSize=15`,
        this.state.search
      )
      .then((response) => {
        console.log(response);
        this.setState(
          {
            results: response.data.content,
            activePage: data.activePage,
          },
          () => {
            console.log(this.state);
          }
        );
      });
  };

  handleSort = (sortBy) => {
    var search = this.state.search;
    search.sortBy = sortBy;
    if (this.state.sortBy === sortBy) {
      search.direction = this.state.direction === "asce" ? "desc" : "asce";
    } else {
      search.direction = "asce";
    }

    axios
      .post(
        `http://10.171.204.164:8080/documents/user-search?pageNum=${
          this.state.activePage - 1
        }&pageSize=15`,
        search
      )
      .then((response) => {
        this.setState({
          results: response.data.content,
          direction: search.direction,
          sortBy: sortBy,
        });
      });
  };

  render() {
    // the variables in the state that are being changed by user inputs
    // excluding the variables within the inputs that change
    // dynamically
    const {
      collection,
      dateFrom,
      dateTo,
      status,
      typeDesc,
      langDesc,
      religion,
      dropdownReligion,
    } = this.state;

    const selectReligion = [
      {
        key: "Religion",
        text: "Religion",
        value: "",
      },
    ].concat(dropdownReligion);

    var view;
    const activeIndex = this.state.activeIndex;

    // Go to AddSearch.js to see the inputs that can change dynamically
    return (
      <div>
        <Transition
          visible={!this.state.pushed}
          animation="fade right"
          duration={500}
        >
          <Segment style={{ backgroundColor: "#F9FAFB" }}>
            <Form onSubmit={this.onSubmit}>
              <Header style={headerStyle} dividing>
                Document Information
              </Header>
              <Form.Group widths="equal">
                <Form.Input
                  fluid
                  label="Collection"
                  name="collection"
                  value={collection}
                  placeholder="Collection"
                  onChange={this.handleChange}
                />
                <Form.Input
                  fluid
                  label="Type of Document"
                  name="typeDesc"
                  value={typeDesc}
                  placeholder="Type of Document"
                  onChange={this.handleChange}
                />
              </Form.Group>
              <Form.Group widths="equal">
                <Form.Input
                  fluid
                  label="Language"
                  name="langDesc"
                  value={langDesc}
                  placeholder="Language"
                  onChange={this.handleChange}
                />
                <Form.Select
                  fluid
                  label="Status"
                  options={dropdownStatus}
                  placeholder="Status"
                  onChange={this.onChangeStatus}
                />
                <Form.Select
                  fluid
                  label="Religion"
                  options={selectReligion}
                  placeholder="Religion"
                  onChange={this.onChangeReligion}
                />
              </Form.Group>
              <Header style={headerStyle} dividing>
                Year
              </Header>
              <Form.Group widths="equal">
                <Form.Input
                  fluid
                  label="From"
                  name="dateFrom"
                  value={dateFrom}
                  placeholder="From"
                  onChange={this.handleChange}
                />
                <Form.Input
                  fluid
                  label="To"
                  name="dateTo"
                  value={dateTo}
                  placeholder="To"
                  onChange={this.handleChange}
                />
              </Form.Group>
              {this.state.errors.dates ? (
                <Message negative>{this.state.errors.dates}</Message>
              ) : (
                ""
              )}
              <Header style={headerStyle} dividing>
                People
              </Header>
              <AddPersonSearch
                handleUpdate={this.handlePersonSearch}
                pushed={this.state.pushed}
              />
              <Header style={headerStyle} dividing>
                Places
              </Header>
              <AddPlaceSearch
                handleUpdate={this.handlePlacesSearch}
                pushed={this.state.pushed}
              />
              <Header />
              <Grid columns={2}>
                <GridColumn>
                  <Header style={headerStyle} dividing>
                    Organizations
                  </Header>
                  <AddOrganizationSearch
                    handleUpdate={this.handleOrganizationSearch}
                  />
                </GridColumn>
                <GridColumn>
                  <Header style={headerStyle} dividing>
                    Keywords
                  </Header>
                  <AddKeywordSearch handleUpdate={this.handleKeywordSearch} />
                </GridColumn>
              </Grid>
              <Button fluid type="submit" value="Submit">
                Search
              </Button>
            </Form>
          </Segment>
        </Transition>
        <Transition
          visible={this.state.pushed}
          animation="fade right"
          duration={1500}
        >
          <Grid>
            <GridColumn width={4}>
              <Segment style={{ backgroundColor: "#F9FAFB" }}>
                <Form onSubmit={() => this.onSubmit()}>
                  <Accordion fluid>
                    <Accordion.Title
                      active={activeIndex === 0}
                      index={0}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      Document Info
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 0}>
                      <Form.Input
                        fluid
                        label="Collection"
                        name="collection"
                        value={collection}
                        placeholder="Collection"
                        onChange={this.handleChange}
                      />
                      <Form.Input
                        fluid
                        label="Type of Document"
                        name="typeDesc"
                        value={typeDesc}
                        placeholder="Type of Document"
                        onChange={this.handleChange}
                      />
                      <Form.Input
                        fluid
                        label="Language"
                        name="langDesc"
                        value={langDesc}
                        placeholder="Language"
                        onChange={this.handleChange}
                      />
                      <Form.Select
                        fluid
                        label="Status"
                        options={dropdownStatus}
                        placeholder="Status"
                        onChange={this.onChangeStatus}
                      />
                      <Form.Select
                        fluid
                        label="Religion"
                        options={selectReligion}
                        placeholder="Religion"
                        onChange={this.onChangeReligion}
                      />
                    </Accordion.Content>
                    <Accordion.Title
                      active={activeIndex === 1}
                      index={1}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      Year
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 1}>
                      <Form.Input
                        fluid
                        label="From"
                        name="dateFrom"
                        value={dateFrom}
                        placeholder="From"
                        onChange={this.handleChange}
                      />
                      <Form.Input
                        fluid
                        label="To"
                        name="dateTo"
                        value={dateTo}
                        placeholder="To"
                        onChange={this.handleChange}
                      />
                      {this.state.errors.dates ? (
                        <Message negative>{this.state.errors.dates}</Message>
                      ) : (
                        ""
                      )}
                    </Accordion.Content>
                    <Accordion.Title
                      active={activeIndex === 2}
                      index={2}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      People
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 2}>
                      <AddPersonSearch
                        handleUpdate={this.handlePersonSearch}
                        pushed={this.state.pushed}
                      />
                    </Accordion.Content>
                    <Accordion.Title
                      active={activeIndex === 3}
                      index={3}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      Places
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 3}>
                      <AddPlaceSearch
                        handleUpdate={this.handlePlacesSearch}
                        pushed={this.state.pushed}
                      />
                    </Accordion.Content>
                    <Accordion.Title
                      active={activeIndex === 4}
                      index={4}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      Organizations
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 4}>
                      <AddOrganizationSearch
                        handleUpdate={this.handleOrganizationSearch}
                        pushed={this.state.pushed}
                      />
                    </Accordion.Content>
                    <Accordion.Title
                      active={activeIndex === 5}
                      index={5}
                      onClick={this.handleAccordion}
                      style={titleStyle}
                    >
                      <Icon name="dropdown" />
                      Keywords
                    </Accordion.Title>
                    <Accordion.Content active={activeIndex === 5}>
                      <AddKeywordSearch
                        handleUpdate={this.handleKeywordSearch}
                        pushed={this.state.pushed}
                      />
                    </Accordion.Content>
                    <br />
                  </Accordion>
                  <Button basic primary fluid type="submit" value="Submit">
                    Search
                  </Button>
                </Form>
              </Segment>
            </GridColumn>
            <GridColumn width={12}>
              <Segment>
                <Table attached="top" sortable>
                  <Table.Header>
                    <Table.Row>
                      <Table.HeaderCell
                        onClick={() => {
                          this.handleSort("importID");
                        }}
                      >
                        Import ID
                      </Table.HeaderCell>
                      <Table.HeaderCell
                        onClick={() => {
                          this.handleSort("langDesc");
                        }}
                      >
                        Language
                      </Table.HeaderCell>
                      <Table.HeaderCell
                        onClick={() => {
                          this.handleSort("collection");
                        }}
                      >
                        Collection
                      </Table.HeaderCell>
                      <Table.HeaderCell
                        onClick={() => {
                          this.handleSort("typeDesc");
                        }}
                      >
                        Type
                      </Table.HeaderCell>
                      <Table.HeaderCell
                        onClick={() => {
                          this.handleSort("status");
                        }}
                      >
                        Status
                      </Table.HeaderCell>
                    </Table.Row>
                  </Table.Header>
                  <Table.Body>
                    {this.state.results.map((document) => {
                      let type, lang;
                      if (document.docType == null) {
                        type = "";
                      } else {
                        type = document.docType.typeDesc;
                      }
                      if (document.docLanguage == null) {
                        lang = "";
                      } else {
                        lang = document.docLanguage.langDesc;
                      }
                      return (
                        <Table.Row key={document.documentID}>
                          <Table.Cell>{document.importID}</Table.Cell>
                          <Table.Cell>{lang}</Table.Cell>
                          <Table.Cell>{document.collection}</Table.Cell>
                          <Table.Cell>{type}</Table.Cell>
                          <Table.Cell>{document.status}</Table.Cell>
                        </Table.Row>
                      );
                    })}
                  </Table.Body>
                </Table>
                <Pagination
                  attached="bottom"
                  size="mini"
                  style={{
                    width: "600px",
                    margin: "-1px",
                    justifyContent: "center",
                    backgroundColor: "#F9FAFB",
                  }}
                  activePage={this.state.activePage}
                  totalPages={this.state.totalPages}
                  onPageChange={this.handlePageChange}
                  fluid
                  pointing
                  secondary
                />
              </Segment>
            </GridColumn>
          </Grid>
        </Transition>
      </div>
    );
  }
}

export default RobustSearch;
