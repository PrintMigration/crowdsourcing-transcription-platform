import React from "react";
import axios from "axios";
import { Button } from "semantic-ui-react";

axios.defaults.baseURL = "http//10.171.204.164:9011/";
axios.defaults.headers.common["Authorization"] =
  "oZPh08pHTdb5QIsqloZin1o4xLFXA_2dbjOZZ7fHHVM";
axios.defaults.headers.post["Content-Type"] = "application/json";

export default class Logout extends React.Component {
  handleLogout = () => {
    axios.post(`http://10.171.204.164:9011/api/logout`).then(res => {
      console.log("succesful logout...?");
      console.log(res);
    });
  };

  render() {
    return <Button onClick={this.handleLogout}>Logout</Button>;
  }
}
