
Pod::Spec.new do |spec|
  spec.name         = 'PlayerKit'
  spec.version      = '1.0.0'
  spec.summary      = 'PlayerKit'
  spec.description  = 'PlayerKit ..'
  spec.homepage     = 'https://github.com/volcengine'
  spec.license      = { :type => 'MIT', :file => 'LICENSE' }
  spec.author       = { 'author' => 'volcengine rtc' }
  spec.source       = { :path => './'}
  spec.ios.deployment_target = '9.0'
  spec.pod_target_xcconfig = {'CODE_SIGN_IDENTITY' => ''}
  spec.source_files = '**/*.{h,m,c,mm,a}'
  spec.resource_bundles = {
    'BytePlayer' => ['Resource/*.xcassets']
  }
  spec.prefix_header_contents = '#import "Masonry.h"',
                                '#import "ToolKit.h"'
    
  spec.dependency 'ToolKit'
  spec.dependency 'Masonry'
  spec.dependency 'IJKMediaFramework'
end
