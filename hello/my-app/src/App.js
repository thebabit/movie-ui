import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ListView,
} from 'react-native';

import {StackNavigator,} from 'react-navigation';
import Genders from './Screens/Genders';
import Movies from './Screens/Movies';

import DetailMovie from './Screens/DetailMovie';

export default class App extends Component {
  render() {
    return <RootStack/>;
  }
}

const RootStack = StackNavigator(
  {
    genders: {screen: Genders},
    movies: {screen: Movies},
    detailMovie: {screen: DetailMovie}
  },
  {
    initialRouteName: 'genders',
  }
);