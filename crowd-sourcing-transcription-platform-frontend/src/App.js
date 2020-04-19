import React from "react";
import Home from "./Home";
import Header from "./Header";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Browse from "./Browse";
import Transcribe from "./Transcribe";
import Metadata from "./Metadata";
import ManualUpload from "./ManualUpload";
import RobustSearch from "./RobustSearch";
import NotFoundPage from "./NotFoundPage";
import Tips from "./Tips";
import UserPage from "./UserPage";
import DocumentPage from "./DocumentPage";
import Test from "./Test";
import UserList from "./UserList";
import PersonPage from "./PersonPage";
import Edit from "./Edit";
import EditMetadata from "./EditMetadata";
import CookieBanner from "./CookieBanner";

const style = {
  maxWidth: "72rem",
  margin: "auto",
  height: "100vh",
  display: "flex",
  flexDirection: "column",
};

class App extends React.Component {
  render() {
    return (
      <Router>
        <div style={style}>
          <Header />
          <Switch>
            <Route exact path="/" component={Home} />
            <Route exact path="/browse" component={Browse} />
            <Route path="/browse/:collection" component={Browse} />
            <Route path="/transcribe/:id" component={Transcribe} />
            <Route exact path="/upload" component={Metadata} />
            <Route exact path="/upload/manual" component={ManualUpload} />
            <Route exact path="/tips" component={Tips} />
            <Route exact path="/search" component={RobustSearch} />
            <Route exact path="/user" component={UserPage} />
            <Route path="/document/:id" component={DocumentPage} />
            <Route path="/people/:id" component={PersonPage} />
            <Route path="/userlist" component={UserList} />
            <Route path="/test" component={Test} />
            <Route path="/edit/:id" component={Edit} />
            <Route path="/metadata/:id" component={EditMetadata} />
            <Route component={NotFoundPage} />
          </Switch>
          <CookieBanner />
        </div>
      </Router>
    );
  }
}

export default App;
