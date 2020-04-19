import React from 'react';
import ReactQuill from 'react-quill';
import 'quill/dist/quill.snow.css';
import './QuillEditor.css';

class QuillEditor extends React.Component {
  modules = {
    toolbar: [
      [{ header: [1, 2, false] }],
      ['bold', 'italic', 'underline', 'strike'],
    ],
  };

  formats = ['header', 'bold', 'italic', 'underline', 'strike'];

  render() {
    return (
      <ReactQuill
        style={{ height: 'calc(100vh - 200px)', fontFamily: 'Abel' }}
        modules={this.modules}
        formats={this.formats}
        onChange={this.props.handleChange}
        value={this.props.text}
        placeholder='This document has not been transcribed yet.'
      />
    );
  }
}

export default QuillEditor;
