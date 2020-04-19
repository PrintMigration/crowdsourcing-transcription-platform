import React from 'react';
import ContentEditable from 'react-contenteditable';

export default class EditableText extends React.Component {
  render() {
    return (
      <div style={{ display: 'flex', margin: '2px' }}>
        <div
          style={{
            width: '30%',
            whiteSpace: 'nowrap',
            padding: '2px',
            fontWeight: 'bold',
            alignItems: 'right',
            textAlign: 'right',
          }}
        >
          {this.props.label}:&nbsp;
        </div>
        <ContentEditable
          style={{
            width: '70%',
            borderWidth: '0px 0px 1px 0px',
            borderColor: this.props.editing ? 'lightgray' : 'white',
            borderStyle: 'dashed',
            padding: '2px 4px',
            lineBreak: 'anywhere',
          }}
          disabled={!this.props.editing}
          html={this.props.placeholder}
          onChange={(e) => {
            this.props.onChange(e, this.props.name);
          }}
        />
      </div>
    );
  }
}
