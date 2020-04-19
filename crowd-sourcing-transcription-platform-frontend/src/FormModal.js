import React from 'react';
import { Menu, Modal, Transition, Button } from 'semantic-ui-react';
import Login from './Login';
import Register from './Register';
import PasswordResetStart from './PasswordResetStart';

const stylessButton = {
  border: 'none',
  padding: 0,
  'margin-top': 10,
  background: 'none',
  cursor: 'pointer'
};

export default class FormModal extends React.Component {
  state = {
    login: true,
    reset: false
  };

  handleRenderLogin = () => {
    this.setState({ login: true, reset: false });
  };

  handleRenderRegister = () => {
    this.setState({ login: false, reset: false });
  };

  handleRenderReset = () => {
    this.setState({ login: false, reset: true });
  };

  handleOnClose = () => {
    this.setState({ login: true, reset: false });
    this.props.onClose();
  };

  render() {
    return (
      <Transition visible={this.props.open} animation='zoom' duration='500'>
        <Modal
          onClose={this.handleOnClose}
          open={this.props.open}
          size='mini'
          dimmer='blurring'
        >
          <Modal.Content>
            {this.state.reset ? (
              <Button onClick={this.handleRenderLogin} icon='left arrow' />
            ) : (
              <Menu pointing secondary fluid widths={3}>
                <Menu.Item
                  style={{
                    textTransform: 'uppercase',
                    fontFamily: 'LTC-Bodoni-175',
                    fontWeight: 'bold'
                  }}
                  active={this.state.login}
                  onClick={this.handleRenderLogin}
                >
                  Login
                </Menu.Item>
                <Menu.Item
                  style={{
                    textTransform: 'uppercase',
                    fontFamily: 'LTC-Bodoni-175',
                    fontWeight: 'bold'
                  }}
                  active={!this.state.login}
                  onClick={this.handleRenderRegister}
                >
                  Register
                </Menu.Item>
              </Menu>
            )}
            {this.state.reset ? (
              <PasswordResetStart />
            ) : this.state.login ? (
              <Login handleLogIn={this.props.handleLogIn} />
            ) : (
              <Register />
            )}
            {this.state.login && (
              <Button
                style={{
                  backgroundColor: 'transparent',
                  paddingLeft: '0px',
                  color: '#367bc3'
                }}
                onClick={this.handleRenderReset}
              >
                Forgot your password?
              </Button>
            )}
          </Modal.Content>
        </Modal>
      </Transition>
    );
  }
}
