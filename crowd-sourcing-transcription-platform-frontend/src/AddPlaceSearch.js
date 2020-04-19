import React from 'react';
import { Form } from 'semantic-ui-react';

export default class AddPlaceSearch extends React.Component {
  state = {
    places: [],
    townCity: '',
    stateProv: '',
    county: '',
    country: '',
  };

  handleChange = (e, { name, value }) => {
    this.setState({ [name]: value }, () => {
      this.props.handleUpdate(
        [
          {
            place: {
              country: this.state.country,
              county: this.state.county,
              placeDesc: '',
              placeLOD: '',
              stateProv: this.state.stateProv,
              townCity: this.state.townCity,
            },
            role: {
              roleDesc: '',
            },
          },
        ].concat(this.state.places)
      );
    });
  };

  handleEdit = (index, event) => {
    const places = [...this.state.places];
    places[index].place[event.target.name] = event.target.value;
    this.setState({ places }, () => {
      this.props.handleUpdate(
        [
          {
            place: {
              country: this.state.country,
              county: this.state.county,
              placeDesc: '',
              placeLOD: '',
              stateProv: this.state.stateProv,
              townCity: this.state.townCity,
            },
            role: {
              roleDesc: '',
            },
          },
        ].concat(this.state.places)
      );
    });
  };

  handleAdd = () => {
    this.setState(
      {
        places: [
          ...this.state.places,
          {
            place: {
              country: this.state.country,
              county: this.state.county,
              placeDesc: '',
              placeLOD: '',
              stateProv: this.state.stateProv,
              townCity: this.state.townCity,
            },
            role: {
              roleDesc: '',
            },
          },
        ],
        townCity: '',
        stateProv: '',
        county: '',
        country: '',
      },
      () => {
        this.props.handleUpdate(
          [
            {
              place: {
                country: this.state.country,
                county: this.state.county,
                placeDesc: '',
                placeLOD: '',
                stateProv: this.state.stateProv,
                townCity: this.state.townCity,
              },
              role: {
                roleDesc: '',
              },
            },
          ].concat(this.state.places)
        );
      }
    );
  };

  handleDelete = (index) => {
    this.state.places.splice(index, 1);
    this.setState({ places: this.state.places }, () => {
      this.props.handleUpdate(
        [
          {
            place: {
              country: this.state.country,
              county: this.state.county,
              placeDesc: '',
              placeLOD: '',
              stateProv: this.state.stateProv,
              townCity: this.state.townCity,
            },
            role: {
              roleDesc: '',
            },
          },
        ].concat(this.state.places)
      );
    });
  };

  render() {
    let view;
    if (this.props.pushed) {
      view = (
        <div>
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='townCity'
            label='City'
            placeholder='City'
            value={this.state.townCity}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='stateProv'
            label='Province'
            placeholder='Province'
            value={this.state.stateProv}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='county'
            label='County'
            placeholder='County'
            value={this.state.county}
          />
          <Form.Input
            onChange={this.handleChange}
            fluid
            name='country'
            label='Country'
            placeholder='Country'
            value={this.state.country}
          />
          <Form.Button fluid icon='add' onClick={this.handleAdd} />
          {this.state.places.map((place, index) => {
            return (
              <div key={index}>
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='townCity'
                  label='City'
                  value={place.place.townCity}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='stateProv'
                  label='Province'
                  value={place.place.stateProv}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='county'
                  label='County'
                  value={place.place.county}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  fluid
                  name='country'
                  label='Country'
                  value={place.place.country}
                />
                <Form.Button
                  fluid
                  icon='minus'
                  onClick={() => {
                    this.handleDelete(index);
                  }}
                />
              </div>
            );
          })}
        </div>
      );
    } else {
      view = (
        <div>
          <Form.Group>
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='townCity'
              label='City'
              placeholder='City'
              value={this.state.townCity}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='stateProv'
              label='Province'
              placeholder='Province'
              value={this.state.stateProv}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='county'
              label='County'
              placeholder='County'
              value={this.state.county}
            />
            <Form.Input
              onChange={this.handleChange}
              width={4}
              fluid
              name='country'
              label='Country'
              placeholder='Country'
              value={this.state.country}
            />
            <Form.Button
              type='button'
              style={{ margin: '27px 0px 0px 0px' }}
              icon='add'
              circular
              onClick={this.handleAdd}
            />
          </Form.Group>
          {this.state.places.map((place, index) => {
            return (
              <Form.Group key={index}>
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='townCity'
                  label='City'
                  value={place.place.townCity}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='stateProv'
                  label='Province'
                  value={place.place.stateProv}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='county'
                  label='County'
                  value={place.place.county}
                />
                <Form.Input
                  onChange={(event) => this.handleEdit(index, event)}
                  width={4}
                  fluid
                  name='country'
                  label='Country'
                  value={place.place.country}
                />
                <Form.Button
                  type='button'
                  style={{ margin: '27px 0px 0px 0px' }}
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

    return <div>{view}</div>;
  }
}
