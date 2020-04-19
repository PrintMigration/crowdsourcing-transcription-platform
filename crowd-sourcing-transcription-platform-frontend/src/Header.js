import React from 'react';
import { Menu, Dropdown, Icon } from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import Axios from 'axios';
import logo from './assets/logo.png';
import './Header.css';
import FormModal from './FormModal';

// Styles
const menuStyle = {
  fontFamily: 'LTC-Bodoni-175',
  fontSize: '13px',
  borderRadius: '0px',
  boxShadow: 'none',
  border: 'none',
  padding: '2.5rem 0 2.5rem 0',
};
const menuItemStyle = {
  textTransform: 'uppercase',
  fontWeight: 'bold',
  fontStyle: 'italic',
  border: 'none',
};
const dropdownMenuStyle = {
  fontWeight: 'normal',
  fontStyle: 'normal',
  fontFamily: 'Abel',
  boxShadow: 'none',
  border: 'none',
};

class Header extends React.Component {
  state = {
    loginOpen: false,
    collections: [],
    email: null,
  };

  componentDidMount() {
    Axios.get('http://10.171.204.164:8080/documents/collection').then(
      (response) => {
        this.setState({ collections: response.data });
      }
    );

    Axios.get('http://10.171.204.164:8080/fusionauth/user', {
      withCredentials: true,
    })
      .then((response) => {
        this.setState({ email: response.data.email });
      })
      .catch((error) => {});
  }

  onLoginOpen = () => this.setState({ loginOpen: true });
  onLoginClose = () => this.setState({ loginOpen: false });

  handleLogOut = () => {
    Axios.post('http://10.171.204.164:8080/fusionauth/logout').then(
      (response) => {
        console.log(response);
        this.setState({ email: null });
      }
    );
  };

  handleLogIn = (email) => {
    this.setState({
      email: email,
      loggedIn: true,
      loginOpen: false,
    });
  };

  render() {
    let user;
    //console.log(this.state.email);
    if (this.state.email != null) {
      user = (
        <Dropdown simple item icon='user'>
          <Dropdown.Menu style={dropdownMenuStyle} direction='left'>
            <Dropdown.Header>{this.state.email}</Dropdown.Header>
            <Dropdown.Item as={Link} to='/user'>
              Settings
            </Dropdown.Item>
            <Dropdown.Item as={Link} to='/upload'>
              Upload
            </Dropdown.Item>
            <Dropdown.Item onClick={this.handleLogOut}>Sign Out</Dropdown.Item>
          </Dropdown.Menu>
        </Dropdown>
      );
    } else {
      user = (
        <Menu.Item
          style={{
            textTransform: 'uppercase',
            fontWeight: 'bold',
            border: 'none',
            color: '#1a8041',
          }}
          onClick={this.onLoginOpen}
        >
          Sign In
        </Menu.Item>
      );
    }

    return (
      <div className='header'>
        <Menu className='headertest' style={menuStyle} borderless={true}>
          <Menu.Item>
            <img src={logo} alt='logo' />
          </Menu.Item>
          <Menu.Item style={menuItemStyle} position='right' as={Link} to='/'>
            Home
          </Menu.Item>
          <Dropdown simple item text='Collections' style={menuItemStyle}>
            <Dropdown.Menu style={dropdownMenuStyle}>
              {this.state.collections.map((collection, index) => {
                return (
                  <Dropdown.Item
                    as={Link}
                    key={index}
                    to={`/browse/${collection}`}
                  >
                    {collection}
                  </Dropdown.Item>
                );
              })}
            </Dropdown.Menu>
          </Dropdown>
          <Menu.Item style={menuItemStyle} as={Link} to='/browse'>
            Browse
          </Menu.Item>
          <Menu.Item style={menuItemStyle} as={Link} to='/tips'>
            Tips
          </Menu.Item>
          <Menu.Item style={menuItemStyle} as={Link} to='/search'>
            Search
          </Menu.Item>
          {user}
        </Menu>
        <FormModal
          open={this.state.loginOpen}
          onClose={this.onLoginClose}
          handleLogIn={this.handleLogIn}
        />
      </div>
    );
  }
}

export default Header;
