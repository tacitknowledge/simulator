ActionController::Routing::Routes.draw do |map|
  map.connect "systems/script_languages", :controller => 'systems', :action => 'script_languages'
  map.connect "configuration/export.xml", :controller => 'configuration', :action => 'export'


  map.resources :systems do |system|
    system.resources :conversations do |conversation|

      map.connect "systems/:system_id/conversations/:id/enable", :controller => 'conversations', :action => 'enable'
      map.connect "systems/:system_id/conversations/:id/activate", :controller => 'conversations', :action => 'activate'

      map.connect 'systems/:system_id/conversations/transport_types', :controller => 'conversations', :action => 'transport_types'
      map.connect 'systems/:system_id/conversations/format_types', :controller => 'conversations', :action => 'format_types'

      map.connect 'systems/:system_id/conversations/transport_parameters', :controller => 'conversations', :action => 'transport_parameters'
      map.connect 'systems/:system_id/conversations/format_parameters', :controller => 'conversations', :action => 'format_parameters'

      conversation.resources :scenarios do |scenario|

        map.connect "systems/:system_id/conversations/:conversation_id/scenarios/:id/enable", :controller => 'scenarios', :action => 'enable'
        map.connect "systems/:system_id/conversations/:conversation_id/scenarios/:id/clone", :controller => 'scenarios', :action => 'clone'
      end
    end
  end

  # The priority is based upon order of creation: first created -> highest priority.

  # Sample of regular route:
  #   map.connect 'products/:id', :controller => 'catalog', :action => 'view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   map.purchase 'products/:id/purchase', :controller => 'catalog', :action => 'purchase'
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   map.resources :products

  # Sample resource route with options:
  #   map.resources :products, :member => { :short => :get, :toggle => :post }, :collection => { :sold => :get }

  # Sample resource route with sub-resources:
  #   map.resources :products, :has_many => [ :comments, :sales ], :has_one => :seller
  
  # Sample resource route with more complex sub-resources
  #   map.resources :products do |products|
  #     products.resources :comments
  #     products.resources :sales, :collection => { :recent => :get }
  #   end

  # Sample resource route within a namespace:
  #   map.namespace :admin do |admin|
  #     # Directs /admin/products/* to Admin::ProductsController (app/controllers/admin/products_controller.rb)
  #     admin.resources :products
  #   end

  # You can have the root of your site routed with map.root -- just remember to delete public/systems.html.
  # map.root :controller => "welcome"

  # See how all your routes lay out with "rake routes"

  # Install the default routes as the lowest priority.
  # Note: These default routes make all actions in every controller accessible via GET requests. You should
  # consider removing or commenting them out if you're using named routes and resources.
  map.connect ':controller/:action/:id'
  map.connect ':controller/:action/:id.:format'
end
