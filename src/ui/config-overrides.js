const {override, addWebpackAlias, addBundleVisualizer} = require('customize-cra')
const path = require('path')

module.exports = {
    webpack: override(
        addWebpackAlias({
            '@': path.resolve(__dirname, 'src'),
        }),
        process.env.BUNDLE_VISUALIZE && addBundleVisualizer()
    ),
}
