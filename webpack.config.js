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
                test: /\.(jsx|ts|tsx)$/i,
                exclude: /(node_modules)/,
                use: [
                  {
                    loader: 'babel-loader',
                    options: {
                      presets: [ "@babel/preset-typescript", "@babel/preset-env", "@babel/preset-react"]
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
