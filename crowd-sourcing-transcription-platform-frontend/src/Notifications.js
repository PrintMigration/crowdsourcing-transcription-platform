import React from "react";
import Axios from "axios";
import { Button, Accordion, Icon } from "semantic-ui-react";

class Notifications extends React.Component {
  state = {
    activeIndex: -1,

    pageNum: 0,
    pageSize: 10,
    totalPages: 1,

    userID: null,
    notifications: [],
  };

  componentDidMount = () => {
    Axios.get(`http://10.171.204.164:8080/fusionauth/user`, {
      withCredentials: true,
    })
      .then((response) => {
        this.setState({
          userID: response.data.email,
        });
      })
      .then(() => {
        Axios.get(
          `http://10.171.204.164:8080/notifs/all?userID=${this.state.userID}&pageSize=${this.state.pageSize}&pageNum=${this.state.pageNum}`
        ).then((response) => {
          this.setState({
            notifications: response.data.content,
            totalPages: response.data.totalPages,
          });
          Axios.post(
            `http://10.171.204.164:8080/notifs/mark-as-seen?userID=${this.state.userID}`
          ).then((response) => {});
        });
      });
  };

  handleAccordion = (e, titleProps) => {
    const { index } = titleProps;
    const { activeIndex } = this.state;
    const newIndex = activeIndex === index ? -1 : index;

    this.setState({ activeIndex: newIndex });
  };

  handleUpdate = () => {
    Axios.get(
      `http://10.171.204.164:8080/notifs/all?userID=${this.state.userID}&pageSize=${this.state.pageSize}&pageNum=${this.state.pageNum}`
    ).then((response) => {
      this.setState({
        notifications: response.data.content,
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
    console.log(this.state.pageNum);
    console.log(this.state.totalPages);
    if (this.state.pageNum < this.state.totalPages - 1) {
      const pageNum = this.state.pageNum + 1;
      this.setState({ pageNum }, () => {
        this.handleUpdate();
      });
    }
  };

  render() {
    const { pageNum, totalPages, activeIndex } = this.state;

    return (
      <div>
        <Accordion styled fluid>
          {this.state.notifications.map((notification, index) => {
            return (
              <div
                key={index}
                style={{ fontWeight: notification.seen ? "" : "bold" }}
              >
                <Accordion.Title
                  active={activeIndex === index}
                  index={index}
                  onClick={this.handleAccordion}
                >
                  <Icon name="dropdown" />
                  {notification.notification.notificationText}
                  <br />
                  Date: {notification.notification.date.toLocaleString()}
                </Accordion.Title>
                <Accordion.Content active={activeIndex === index}>
                  Details:{" "}
                  {notification.notification.extraText || "No extra details"}
                </Accordion.Content>
              </div>
            );
          })}
        </Accordion>
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
          "There are no notifications at the moment"
        )}
      </div>
    );
  }
}

export default Notifications;
