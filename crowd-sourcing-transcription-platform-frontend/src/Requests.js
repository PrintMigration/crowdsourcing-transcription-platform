import React from "react";
import Axios from "axios";
import { Button, List } from "semantic-ui-react";

class Requests extends React.Component {
  state = {
    pageNum: 0,
    pageSize: 5,
    totalPages: 1,

    userID: null,
    requests: [],
  };

  componentDidMount = () => {
    Axios.get(`http://10.171.204.164:8080/fusionauth/user`, {
      withCredentials: true,
    })
      .then((response) => {
        //console.log(response);
        this.setState({
          userID: response.data.email,
        });
      })
      .then(() => {
        Axios.get(
          `http://10.171.204.164:8080/requests/all?userID=${this.state.userID}&pageSize=${this.state.pageSize}&pageNum=${this.state.pageNum}`
        ).then((response) => {
          //console.log(response);
          this.setState({
            requests: response.data.content,
            totalPages: response.data.totalPages,
          });
        });
      });
  };

  handleAccept = (index, role, userID) => {
    const approve = {
      role,
      userID,
    };
    Axios.post(`http://10.171.204.164:8080/requests/approve`, approve).then(
      (response) => {
        this.handleUpdate();
        this.state.requests.splice(index, 1);
        this.setState({ requests: this.state.requests });
      }
    );
    this.handleUpdate();
  };

  handleDeny = (index, role, userID) => {
    const deny = {
      role,
      userID,
    };
    Axios.post(`http://10.171.204.164:8080/requests/deny`, deny).then(
      (response) => {
        this.handleUpdate();
        this.state.requests.splice(index, 1);
        this.setState({ requests: this.state.requests });
      }
    );
  };

  handleUpdate = () => {
    Axios.get(
      `http://10.171.204.164:8080/requests/all?userID=${this.state.userID}&pageSize=${this.state.pageSize}&pageNum=${this.state.pageNum}`
    ).then((response) => {
      this.setState({
        requests: response.data.content,
        totalPages: response.data.totalPages,
      });
    });
  };

  handlePrevPage = () => {
    if (this.state.pageNum > 0) {
      const pageNum = this.state.pageNum - 1;
      this.setState({ pageNum }, () => {
        this.handleUpdate();
      });
    }
  };

  handleNextPage = () => {
    if (this.state.pageNum < this.state.totalPages - 1) {
      const pageNum = this.state.pageNum + 1;
      this.setState({ pageNum }, () => {
        this.handleUpdate();
      });
    }
  };

  render() {
    const { totalPages, pageNum } = this.state;

    return (
      <div>
        <h1>Requests</h1>
        <List celled>
          {this.state.requests.map((request, index) => {
            return (
              <List.Item key={index}>
                <List.Content>
                  <List.Header>{request.requester}</List.Header>
                  {request.roleRequested}
                  <Button
                    onClick={() =>
                      this.handleAccept(
                        index,
                        request.roleRequested,
                        request.requester
                      )
                    }
                  >
                    Accept
                  </Button>
                  <Button
                    onClick={() =>
                      this.handleDeny(
                        index,
                        request.roleRequested,
                        request.requester
                      )
                    }
                  >
                    Deny
                  </Button>
                </List.Content>
              </List.Item>
            );
          })}
        </List>
        {totalPages ? (
          <div>
            <Button.Group fluid>
              <Button onClick={this.handlePrevPage} icon="angle left" />
              <Button onClick={this.handleNextPage} icon="angle right" />
            </Button.Group>
            <p>
              Page {pageNum + 1} / {totalPages}
            </p>
          </div>
        ) : (
          "There are no requests at the moment"
        )}
      </div>
    );
  }
}

export default Requests;
