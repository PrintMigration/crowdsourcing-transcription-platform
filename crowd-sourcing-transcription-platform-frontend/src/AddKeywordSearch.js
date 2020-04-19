import React from 'react';
import { Form } from 'semantic-ui-react';

export default class AddKeywordSearch extends React.Component {
  state = { keywords: [], keyword: '' };

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value }, () => {
      this.props.handleUpdate(
        [{ keyword: this.state.keyword }].concat(this.state.keywords)
      );
    });
  };

  handleEdit = (index, event) => {
    const keywords = [...this.state.keywords];
    keywords[index][event.target.name] = event.target.value;
    this.setState({ keywords }, () => {
      this.props.handleUpdate(
        [{ keyword: this.state.keyword }].concat(this.state.keywords)
      );
    });
  };

  handleAdd = () => {
    this.setState(
      {
        keywords: [...this.state.keywords, { keyword: this.state.keyword }],
        keyword: '',
      },
      () => {
        this.props.handleUpdate(
          [{ keyword: this.state.keyword }].concat(this.state.keywords)
        );
      }
    );
  };

  handleDelete = (index) => {
    this.state.keywords.splice(index, 1);
    this.setState({ keywords: this.state.keywords }, () => {
      this.props.handleUpdate(
        [{ keyword: this.state.keyword }].concat(this.state.keywords)
      );
    });
  };

  render() {
    let size, buttonStyle;
    if (this.props.pushed) {
      buttonStyle = { margin: '23px 0px 0px 0px' };
    } else {
      buttonStyle = { margin: '27px 0px 0px 0px' };
    }

    return (
      <div>
        <Form.Group>
          <Form.Input
            onChange={this.handleChange}
            width={16}
            fluid
            name='keyword'
            label='Keyword'
            placeholder='Keyword'
            value={this.state.keyword}
          />
          <Form.Button
            type='button'
            style={buttonStyle}
            icon='add'
            circular
            onClick={this.handleAdd}
          />
        </Form.Group>
        {this.state.keywords.map((keyword, index) => {
          return (
            <Form.Group key={index}>
              <Form.Input
                onChange={(event) => this.handleEdit(index, event)}
                width={16}
                fluid
                name='keyword'
                label='Keyword'
                value={keyword.keyword}
              />
              <Form.Button
                type='button'
                style={buttonStyle}
                icon='minus'
                circular
                onClick={() => {
                  this.handleDelete(index);
                }}
              />
            </Form.Group>
          );
        })}
      </div>
    );
  }
}
