const GEOCODER_KEY_LIST = [
  '66DBZ-4CPCZ-LVLX5-TRNGM-LDXTJ-I5BPQ',
  'JJVBZ-OJK6Z-WMYXE-Z6K2G-MBPLO-MJBTU'
];

/**
 * geocoder 接口单次请求随机分流一个 key，降低单 key 配额压力。
 */
const getRandomGeocoderKey = () => {
  const randomIndex = Math.floor(Math.random() * GEOCODER_KEY_LIST.length);
  return GEOCODER_KEY_LIST[randomIndex];
};

/**
 * 统一拼接逆地理编码请求地址，避免页面里散落硬编码 key。
 */
const getReverseGeocoderUrl = (latitude, longitude) => {
  if (latitude === undefined || latitude === null || longitude === undefined || longitude === null) {
    return '';
  }
  return 'https://apis.map.qq.com/ws/geocoder/v1/?location=' + latitude + ',' + longitude + '&key=' + getRandomGeocoderKey();
};

module.exports = {
  GEOCODER_KEY_LIST,
  getRandomGeocoderKey,
  getReverseGeocoderUrl
};
