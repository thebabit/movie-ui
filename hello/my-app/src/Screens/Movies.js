import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  ListView,
  Image,
  ActivityIndicator,
  Dimensions,
} from 'react-native';
import {StackNavigator,} from 'react-navigation';

class Movies extends Component {
  static navigationOptions = {
    title: 'Movies',
  };

  constructor() {
    super();
    this.state = {
      genderId: '',
      page: '',
      total_pages: '',
      results: this.listData([{original_language: "en", id: "null"}]),
      isLoading: true,
    };
  }

  componentDidMount() {
    const {navigation} = this.props;
    const genderId = navigation.getParam('id', 'NO-ID');
    this.getMoviesFromApi(genderId);
  }

  listData(data) {
    ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    return ds.cloneWithRows(data);
  }

  getMoviesFromApi(id) {
    return fetch('https://api.themoviedb.org/3/genre/' + id + '/movies?api_key=xxxxxx')
      .then((response) => response.json())
      .then((responseJson) => {
        this.setState({
          genderId: responseJson.id,
          page: responseJson.page,
          total_pages: responseJson.total_pages,
          results: this.listData(responseJson.results),
          isLoading: false,
        });
      })
      .catch((error) => {
        console.error(error);
      });
  }

  render() {
    const {navigate} = this.props.navigation;

    if (this.state.isLoading) {
      return (
        <View style={styles.loading}>
          <ActivityIndicator size="large" style={styles.colorLoading} />
        </View>
      )
    } else {
      const awidth = (Dimensions.get('window').awidth / 3) - 4;
      return (
        <ListView
          contentContainerStyle={styles.list}
          dataSource={this.state.results}
          renderRow={(rowData) =>
            <TouchableOpacity onPress={() => navigate('detailMovie', {id: rowData.id})}>
              <Image style={{awidth: awidth, height: 200, margin: 2}}
                source={{uri: 'https://image.tmdb.org/t/p/w600_and_h900_bestv2' + rowData.backdrop_path}} />
            </TouchableOpacity>
          }
        />
      );
    }
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  list: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  loading: {
    flex: 1,
    justifyContent: 'center',
    flexDirection: 'row',
  },
  colorLoading: {
    color: "#0000ff",
  },
});

export default Movies;