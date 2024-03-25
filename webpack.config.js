var path = require('path');

module.exports = {
    entry: './src/main/js/Index.jsx',
    cache: true,
    mode: "development", 
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static/built/'),
        filename: 'bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.jsx$/i,
                use: [
                  {
                    loader: 'babel-loader',
                    options: {
                      presets: ["@babel/preset-env", "@babel/preset-react"]
                    } 
                  }
                ]
            },
            {
                test: /\.css$/i,
                use: [ "style-loader", "css-loader" ]
            }
        ]
    }
};
