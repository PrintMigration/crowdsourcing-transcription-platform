import React from 'react';
import { Document, Page } from 'react-pdf/dist/entry.webpack';
import { Icon, Menu, Popup } from 'semantic-ui-react';
import { TransformWrapper, TransformComponent } from 'react-zoom-pan-pinch';
import './PDFViewer.css';

const topMenuStyle = {
  borderStyle: 'solid',
  borderWidth: '0px 0px 1px 0px',
  borderRadius: '0px',
  margin: '0px',
};

const bottomMenuStyle = {
  borderStyle: 'solid',
  borderWidth: '1px 0px 0px 0px',
  borderRadius: '0px',
  margin: '0px',
};

class PDFViewer extends React.Component {
  constructor(props) {
    super(props);
  }
  state = {
    numPages: null,
    pageNumber: 1,
    rotate: 0,
  };

  handleRotateLeft = () => {
    this.setState((prevState) => ({
      rotate: (prevState.rotate + 270) % 360,
    }));
  };

  handleRotateRight = () => {
    this.setState((prevState) => ({
      rotate: (prevState.rotate + 90) % 360,
    }));
  };

  handleReset = () => {
    this.setState({
      scale: 1.0,
      rotate: 0,
    });
  };

  handlePrevPage = () => {
    if (this.state.pageNumber > 1) {
      this.setState((prevState) => ({
        pageNumber: prevState.pageNumber - 1,
      }));
    }
  };

  handleNextPage = () => {
    if (this.state.pageNumber < this.state.numPages) {
      this.setState((prevState) => ({
        pageNumber: prevState.pageNumber + 1,
      }));
    }
  };

  onDocumentLoadSuccess = ({ numPages }) => {
    this.setState({ numPages });
  };

  render() {
    const { pageNumber, numPages, rotate } = this.state;
    const file = this.props.pdfURL
      ? `https://cors-anywhere.herokuapp.com/${this.props.pdfURL}`
      : null;

    return (
      <div
        style={{
          height: '100%',
          backgroundColor: 'lightgray',
          borderStyle: 'solid',
          borderColor: 'lightgray',
          borderWidth: '1px',
        }}
      >
        <TransformWrapper
          defaultScale={1}
          zoomIn={{ step: 10 }}
          zoomOut={{ step: 10 }}
          options={{ limitToBounds: false, centerContent: false }}
        >
          {({ zoomIn, zoomOut, resetTransform }) => (
            <React.Fragment>
              <Menu style={topMenuStyle} icon borderless attached='top' fluid>
                <Menu.Item onClick={this.handleRotateLeft}>
                  <Icon name='undo' />
                </Menu.Item>
                <Menu.Item onClick={this.handleRotateRight}>
                  <Icon name='redo' />
                </Menu.Item>
                <Menu.Item onClick={zoomIn}>
                  <Icon name='plus' />
                </Menu.Item>
                <Menu.Item onClick={zoomOut}>
                  <Icon name='minus' />
                </Menu.Item>
                <Menu.Menu position='right'>
                  <Menu.Item>
                    <Popup
                      offset='14 10'
                      position='bottom right'
                      content='Scroll to zoom in and out. Pan by click and drag'
                      trigger={<Icon name='question' />}
                    />
                  </Menu.Item>
                  <Menu.Item onClick={resetTransform}>
                    <Icon name='x' />
                  </Menu.Item>
                </Menu.Menu>
              </Menu>
              <TransformComponent>
                <Document
                  file={file}
                  onLoadSuccess={this.onDocumentLoadSuccess}
                  renderMode='svg'
                >
                  <Page
                    renderAnnotationLayer={true}
                    pageNumber={pageNumber}
                    rotate={rotate}
                  />
                </Document>
              </TransformComponent>
              <Menu style={bottomMenuStyle} borderless attached='bottom' fluid>
                <Menu.Item
                  position='left'
                  name='Prev Page'
                  onClick={this.handlePrevPage}
                />
                <Menu.Item disabled>
                  <p>
                    Page {pageNumber} of {numPages}
                  </p>
                </Menu.Item>
                <Menu.Item
                  position='right'
                  name='Next Page'
                  onClick={this.handleNextPage}
                />
              </Menu>
            </React.Fragment>
          )}
        </TransformWrapper>
      </div>
    );
  }
}

export default PDFViewer;
